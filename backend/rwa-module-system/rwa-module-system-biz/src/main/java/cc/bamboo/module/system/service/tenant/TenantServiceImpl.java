package cc.bamboo.module.system.service.tenant;

import cc.bamboo.module.system.controller.admin.publisherinfo.vo.PublisherInfoSaveReqVO;
import cc.bamboo.module.system.dal.mysql.publisherinfo.PublisherInfoMapper;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cc.bamboo.framework.common.enums.CommonStatusEnum;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.collection.CollectionUtils;
import cc.bamboo.framework.common.util.date.DateUtils;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.framework.tenant.config.TenantProperties;
import cc.bamboo.framework.tenant.core.context.TenantContextHolder;
import cc.bamboo.framework.tenant.core.util.TenantUtils;
import cc.bamboo.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantSaveReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantCreateWithAuditReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantSubmitAuditReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantAuditReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantAuditStatusRespVO;

import cc.bamboo.module.system.convert.tenant.TenantConvert;
import cc.bamboo.module.system.dal.dataobject.permission.MenuDO;
import cc.bamboo.module.system.dal.dataobject.permission.RoleDO;
import cc.bamboo.module.system.dal.dataobject.tenant.TenantDO;
import cc.bamboo.module.system.dal.dataobject.tenant.TenantPackageDO;
import cc.bamboo.module.system.dal.dataobject.publisherinfo.PublisherInfoDO;
import cc.bamboo.module.system.dal.mysql.tenant.TenantMapper;
import cc.bamboo.module.system.enums.permission.RoleCodeEnum;
import cc.bamboo.module.system.enums.permission.RoleTypeEnum;
import cc.bamboo.module.system.service.permission.MenuService;
import cc.bamboo.module.system.service.permission.PermissionService;
import cc.bamboo.module.system.service.permission.RoleService;
import cc.bamboo.module.system.service.publisherinfo.PublisherInfoService;
import cc.bamboo.module.system.service.tenant.handler.TenantInfoHandler;
import cc.bamboo.module.system.service.tenant.handler.TenantMenuHandler;
import cc.bamboo.module.system.service.user.AdminUserService;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.system.enums.ErrorCodeConstants.*;
import static java.util.Collections.singleton;

/**
 * 租户 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class TenantServiceImpl implements TenantService {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false) // 由于 rwa.tenant.enable 配置项，可以关闭多租户的功能，所以这里只能不强制注入
    private TenantProperties tenantProperties;

    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private AdminUserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private PublisherInfoService publisherInfoService;

    @Resource
    private PublisherInfoMapper publisherInfoMapper;

    @Override
    public List<Long> getTenantIdList() {
        List<TenantDO> tenants = tenantMapper.selectList();
        return CollectionUtils.convertList(tenants, TenantDO::getId);
    }

    @Override
    public void validTenant(Long id) {
        TenantDO tenant = getTenant(id);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        if (tenant.getStatus().equals(CommonStatusEnum.DISABLE.getStatus())) {
            throw exception(TENANT_DISABLE, tenant.getName());
        }
        if (DateUtils.isExpired(tenant.getExpireTime())) {
            throw exception(TENANT_EXPIRE, tenant.getName());
        }
    }

    @Override
    @DSTransactional // 多数据源，使用 @DSTransactional 保证本地事务，以及数据源的切换
    public Long createTenant(TenantSaveReqVO createReqVO) {
        // 校验租户名称是否重复
        validTenantNameDuplicate(createReqVO.getName(), null);
        // 校验租户域名是否重复
        validTenantWebsiteDuplicate(createReqVO.getWebsite(), null);
        // 校验套餐被禁用
        TenantPackageDO tenantPackage = tenantPackageService.validTenantPackage(createReqVO.getPackageId());

        // 创建租户
        TenantDO tenant = BeanUtils.toBean(createReqVO, TenantDO.class);
        tenantMapper.insert(tenant);
        // 创建租户的管理员
        TenantUtils.execute(tenant.getId(), () -> {
            // 创建角色
            Long roleId = createRole(tenantPackage);
            // 创建用户，并分配角色
            Long userId = createUser(roleId, createReqVO);
            // 修改租户的管理员
            tenantMapper.updateById(new TenantDO().setId(tenant.getId()).setContactUserId(userId));
        });
        return tenant.getId();
    }

    @Override
    @DSTransactional // 多数据源，使用 @DSTransactional 保证本地事务，以及数据源的切换
    public Long createTenantWithAudit(TenantCreateWithAuditReqVO createReqVO) {
        // 校验租户名称是否重复
        validTenantNameDuplicate(createReqVO.getName(), null);
        // 校验租户域名是否重复
        validTenantWebsiteDuplicate(createReqVO.getWebsite(), null);
        // 校验套餐被禁用
        TenantPackageDO tenantPackage = tenantPackageService.validTenantPackage(createReqVO.getPackageId());
        if(StringUtils.isNotBlank(createReqVO.getBankAccountName()) && createReqVO.getAuditStatus() == 2){
            if(!createReqVO.getBankAccountName().equals(createReqVO.getName())
                    && !createReqVO.getBankAccountName().equals(createReqVO.getIdCardName())){
                throw exception(Bank_ACCOUNT_NAME_NOT_MATCH);
            }
        }


        // 创建租户
        TenantDO tenant = new TenantDO();
        tenant.setName(createReqVO.getName());
        tenant.setContactName(createReqVO.getContactName());
        tenant.setContactMobile(createReqVO.getContactMobile());
        tenant.setStatus(createReqVO.getStatus());
        tenant.setWebsite(createReqVO.getWebsite());
        tenant.setPackageId(createReqVO.getPackageId());
        tenant.setExpireTime(createReqVO.getExpireTime());
        tenant.setAccountCount(createReqVO.getAccountCount());
        tenant.setAuditStatus(createReqVO.getAuditStatus());
        tenantMapper.insert(tenant);

        // 创建租户的管理员
        TenantUtils.execute(tenant.getId(), () -> {
            // 创建角色
            Long roleId = createRole(tenantPackage);
            // 创建用户，并分配角色
            Long userId = createUserWithAudit(roleId, createReqVO);
            // 修改租户的管理员
            tenantMapper.updateById(new TenantDO().setId(tenant.getId()).setContactUserId(userId));

            // 始终创建发行商信息，不管审核状态
            createPublisherInfo(userId, createReqVO, tenant);
        });
        return tenant.getId();
    }

    @Override
    public Long getAuditCount() {
        LambdaQueryWrapper<TenantDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TenantDO::getAuditStatus, 1);
        return tenantMapper.selectCount(queryWrapper);
    }

    private Long createUserWithAudit(Long roleId, TenantCreateWithAuditReqVO createReqVO) {
        // 创建用户
        Long userId = userService.createUser(TenantConvert.INSTANCE.convert03(createReqVO));
        // 分配角色
        permissionService.assignUserRole(userId, singleton(roleId));
        return userId;
    }

    private void createPublisherInfo(Long userId, TenantCreateWithAuditReqVO createReqVO, TenantDO tenant) {
        PublisherInfoDO publisherInfo = new PublisherInfoDO();
        publisherInfo.setId(userId);

        // 使用租户名作为公司名，联系手机作为用户手机号
        publisherInfo.setCompanyName(tenant.getName());
        publisherInfo.setUserPhone(tenant.getContactMobile());
        publisherInfo.setTenantId(tenant.getId());
        publisherInfo.setRegisterTime(
                createReqVO.getRegisterTime() != null ? createReqVO.getRegisterTime() : java.time.LocalDateTime.now());

        // 根据审核状态设置身份认证状态
        // auditStatus: 0-待提交审核 -> identityAuthStatus: 0-未认证
        // auditStatus: 2-审核通过 -> identityAuthStatus: 2-已认证
        Integer identityAuthStatus = 0; // 默认未认证
        if (createReqVO.getAuditStatus() != null && createReqVO.getAuditStatus() == 2) {
            identityAuthStatus = 2; // 已认证
        }
        publisherInfo.setIdentityAuthStatus(identityAuthStatus);

        // 设置其他发行商信息（仅当审核通过时才有这些信息）
        if (createReqVO.getAuditStatus() != null && createReqVO.getAuditStatus() == 2) {
            publisherInfo.setAuthIdentity(createReqVO.getAuthIdentity());
            publisherInfo.setCompanyCreditCode(createReqVO.getCompanyCreditCode());
            publisherInfo.setBusinessLicenseUrl(createReqVO.getBusinessLicenseUrl());
            publisherInfo.setQualificationFileUrls(createReqVO.getQualificationFileUrls());
            publisherInfo.setAuthorizationFileUrls(createReqVO.getAuthorizationFileUrls());
            publisherInfo.setIdCardName(createReqVO.getIdCardName());
            publisherInfo.setIdCardNo(createReqVO.getIdCardNo());
            publisherInfo.setIdCardExpireTime(createReqVO.getIdCardExpireTime());
            publisherInfo.setIdCardFrontUrl(createReqVO.getIdCardFrontUrl());
            publisherInfo.setIdCardBackUrl(createReqVO.getIdCardBackUrl());
            publisherInfo.setEmail(createReqVO.getEmail());
            publisherInfo.setBankAccountName(createReqVO.getBankAccountName());
            publisherInfo.setBankAccount(createReqVO.getBankAccount());
            publisherInfo.setBankName(createReqVO.getBankName());
        }

        publisherInfoMapper.insert(publisherInfo);
    }

    private Long createUser(Long roleId, TenantSaveReqVO createReqVO) {
        // 创建用户
        Long userId = userService.createUser(TenantConvert.INSTANCE.convert02(createReqVO));
        // 分配角色
        permissionService.assignUserRole(userId, singleton(roleId));
        return userId;
    }

    private Long createRole(TenantPackageDO tenantPackage) {
        // 创建角色
        RoleSaveReqVO reqVO = new RoleSaveReqVO();
        reqVO.setName(RoleCodeEnum.TENANT_ADMIN.getName()).setCode(RoleCodeEnum.TENANT_ADMIN.getCode())
                .setSort(0).setRemark("系统自动生成");
        Long roleId = roleService.createRole(reqVO, RoleTypeEnum.SYSTEM.getType());
        // 分配权限
        permissionService.assignRoleMenu(roleId, tenantPackage.getMenuIds());
        return roleId;
    }

    @Override
    @DSTransactional // 多数据源，使用 @DSTransactional 保证本地事务，以及数据源的切换
    public void updateTenant(TenantSaveReqVO updateReqVO) {
        // 校验存在
        TenantDO tenant = validateUpdateTenant(updateReqVO.getId());
        // 校验租户名称是否重复
        validTenantNameDuplicate(updateReqVO.getName(), updateReqVO.getId());
        // 校验租户域名是否重复
        validTenantWebsiteDuplicate(updateReqVO.getWebsite(), updateReqVO.getId());
        // 校验套餐被禁用
        TenantPackageDO tenantPackage = tenantPackageService.validTenantPackage(updateReqVO.getPackageId());

        // 更新租户
        TenantDO updateObj = BeanUtils.toBean(updateReqVO, TenantDO.class);
        tenantMapper.updateById(updateObj);
        // 如果套餐发生变化，则修改其角色的权限
        if (ObjectUtil.notEqual(tenant.getPackageId(), updateReqVO.getPackageId())) {
            updateTenantRoleMenu(tenant.getId(), tenantPackage.getMenuIds());
        }
    }

    private void validTenantNameDuplicate(String name, Long id) {
        TenantDO tenant = tenantMapper.selectByName(name);
        if (tenant == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同名字的租户
        if (id == null) {
            throw exception(TENANT_NAME_DUPLICATE, name);
        }
        if (!tenant.getId().equals(id)) {
            throw exception(TENANT_NAME_DUPLICATE, name);
        }
    }

    private void validTenantWebsiteDuplicate(String website, Long id) {
        if (StrUtil.isEmpty(website)) {
            return;
        }
        TenantDO tenant = tenantMapper.selectByWebsite(website);
        if (tenant == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同名字的租户
        if (id == null) {
            throw exception(TENANT_WEBSITE_DUPLICATE, website);
        }
        if (!tenant.getId().equals(id)) {
            throw exception(TENANT_WEBSITE_DUPLICATE, website);
        }
    }

    @Override
    @DSTransactional
    public void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds) {
        TenantUtils.execute(tenantId, () -> {
            // 获得所有角色
            List<RoleDO> roles = roleService.getRoleList();
            roles.forEach(role -> Assert.isTrue(tenantId.equals(role.getTenantId()), "角色({}/{}) 租户不匹配",
                    role.getId(), role.getTenantId(), tenantId)); // 兜底校验
            // 重新分配每个角色的权限
            roles.forEach(role -> {
                // 如果是租户管理员，重新分配其权限为租户套餐的权限
                if (Objects.equals(role.getCode(), RoleCodeEnum.TENANT_ADMIN.getCode())) {
                    permissionService.assignRoleMenu(role.getId(), menuIds);
                    log.info("[updateTenantRoleMenu][租户管理员({}/{}) 的权限修改为({})]", role.getId(), role.getTenantId(),
                            menuIds);
                    return;
                }
                // 如果是其他角色，则去掉超过套餐的权限
                Set<Long> roleMenuIds = permissionService.getRoleMenuListByRoleId(role.getId());
                roleMenuIds = CollUtil.intersectionDistinct(roleMenuIds, menuIds);
                permissionService.assignRoleMenu(role.getId(), roleMenuIds);
                log.info("[updateTenantRoleMenu][角色({}/{}) 的权限修改为({})]", role.getId(), role.getTenantId(), roleMenuIds);
            });
        });
    }

    @Override
    public void deleteTenant(Long id) {
        // 校验存在
        validateUpdateTenant(id);
        // 删除
        tenantMapper.deleteById(id);
    }

    private TenantDO validateUpdateTenant(Long id) {
        TenantDO tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        // 内置租户，不允许删除
        if (isSystemTenant(tenant)) {
            throw exception(TENANT_CAN_NOT_UPDATE_SYSTEM);
        }
        return tenant;
    }

    @Override
    public TenantDO getTenant(Long id) {
        return tenantMapper.selectById(id);
    }

    @Override
    public PageResult<TenantDO> getTenantPage(TenantPageReqVO pageReqVO) {
        return tenantMapper.selectPage(pageReqVO);
    }

    @Override
    public TenantDO getTenantByName(String name) {
        return tenantMapper.selectByName(name);
    }

    @Override
    public TenantDO getTenantByWebsite(String website) {
        return tenantMapper.selectByWebsite(website);
    }

    @Override
    public Long getTenantCountByPackageId(Long packageId) {
        return tenantMapper.selectCountByPackageId(packageId);
    }

    @Override
    public List<TenantDO> getTenantListByPackageId(Long packageId) {
        return tenantMapper.selectListByPackageId(packageId);
    }

    @Override
    public void handleTenantInfo(TenantInfoHandler handler) {
        // 如果禁用，则不执行逻辑
        if (isTenantDisable()) {
            return;
        }
        // 获得租户
        TenantDO tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        // 执行处理器
        handler.handle(tenant);
    }

    @Override
    public void handleTenantMenu(TenantMenuHandler handler) {
        // 如果禁用，则不执行逻辑
        if (isTenantDisable()) {
            return;
        }
        // 获得租户，然后获得菜单
        TenantDO tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        Set<Long> menuIds;
        if (isSystemTenant(tenant)) { // 系统租户，菜单是全量的
            menuIds = CollectionUtils.convertSet(menuService.getMenuList(), MenuDO::getId);
        } else {
            menuIds = tenantPackageService.getTenantPackage(tenant.getPackageId()).getMenuIds();
        }
        // 执行处理器
        handler.handle(menuIds);
    }

    private static boolean isSystemTenant(TenantDO tenant) {
        return Objects.equals(tenant.getPackageId(), TenantDO.PACKAGE_ID_SYSTEM);
    }

    private boolean isTenantDisable() {
        return tenantProperties == null || Boolean.FALSE.equals(tenantProperties.getEnable());
    }

    // ========== 审核相关方法实现 ==========

    @Override
    @DSTransactional
    public void submitAudit(TenantSubmitAuditReqVO reqVO) {
        // 获取当前租户ID
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        TenantDO tenant = tenantMapper.selectById(tenantId);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        if(!reqVO.getBankAccountName().equals(tenant.getName())
                && !reqVO.getBankAccountName().equals(reqVO.getIdCardName())){
            throw exception(Bank_ACCOUNT_NAME_NOT_MATCH);
        }
        // 校验审核状态：只有待提交(0)或被拒绝(3)才能提交
        if (tenant.getAuditStatus() != null && tenant.getAuditStatus() != 0 && tenant.getAuditStatus() != 3) {
            throw exception(TENANT_NOT_EXISTS, "当前状态不允许提交审核");
        }

        // 更新发行商信息（发行商信息在创建租户时已创建）
        TenantUtils.execute(tenantId, () -> {
            // 获取已存在的发行商信息
            PublisherInfoDO publisherInfo = publisherInfoService.getPublisherInfoByUserId(tenant.getContactUserId());
            if (publisherInfo == null) {
                throw exception(TENANT_NOT_EXISTS, "发行商信息不存在");
            }

            // 更新发行商信息
            publisherInfo.setCompanyName(tenant.getName());
            publisherInfo.setUserPhone(tenant.getContactMobile());
            publisherInfo.setCompanyCreditCode(reqVO.getCompanyCreditCode());
            publisherInfo.setBusinessLicenseUrl(reqVO.getBusinessLicenseUrl());
            publisherInfo.setQualificationFileUrls(reqVO.getQualificationFileUrls());
            publisherInfo.setAuthorizationFileUrls(reqVO.getAuthorizationFileUrls());
            publisherInfo.setAuthIdentity(reqVO.getAuthIdentity());
            publisherInfo.setIdCardName(reqVO.getIdCardName());
            publisherInfo.setIdCardNo(reqVO.getIdCardNo());
            publisherInfo.setIdCardExpireTime(reqVO.getIdCardExpireTime());
            publisherInfo.setIdCardFrontUrl(reqVO.getIdCardFrontUrl());
            publisherInfo.setIdCardBackUrl(reqVO.getIdCardBackUrl());
            publisherInfo.setEmail(reqVO.getEmail());
            publisherInfo.setBankAccountName(reqVO.getBankAccountName());
            publisherInfo.setBankAccount(reqVO.getBankAccount());
            publisherInfo.setBankName(reqVO.getBankName());
            publisherInfo.setIdentityAuthStatus(1); // 认证中

            // 更新发行商信息（而非新增）
            publisherInfoService.updatePublisherInfo(BeanUtils.toBean(publisherInfo,
                    PublisherInfoSaveReqVO.class));
        });

        // 更新租户审核状态为待审核
        tenantMapper.updateById(new TenantDO().setId(tenantId).setAuditStatus(1));
    }

    @Override
    @DSTransactional
    public void auditTenant(TenantAuditReqVO reqVO) {
        // 校验租户存在
        TenantDO tenant = tenantMapper.selectById(reqVO.getTenantId());
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }

        // 校验审核状态：只有待审核(1)才能审核
        if (tenant.getAuditStatus() == null || tenant.getAuditStatus() != 1) {
            throw exception(TENANT_NOT_EXISTS, "租户不在待审核状态");
        }

        // 校验审核结果
        if (reqVO.getAuditResult() != 2 && reqVO.getAuditResult() != 3) {
            throw exception(TENANT_NOT_EXISTS, "审核结果无效");
        }

        // 如果是拒绝，必须填写备注
        if (reqVO.getAuditResult() == 3 && (reqVO.getAuditRemark() == null || reqVO.getAuditRemark().isEmpty())) {
            throw exception(TENANT_NOT_EXISTS, "拒绝时必须填写审核备注");
        }

        // 更新租户审核状态
        TenantDO updateTenant = new TenantDO();
        updateTenant.setId(reqVO.getTenantId());
        updateTenant.setAuditStatus(reqVO.getAuditResult());
        updateTenant.setAuditRemark(reqVO.getAuditRemark());
        tenantMapper.updateById(updateTenant);

        // 如果通过，更新发行商认证状态
        if (reqVO.getAuditResult() == 2) {
            TenantUtils.execute(reqVO.getTenantId(), () -> {
                PublisherInfoDO publisherInfo = publisherInfoService
                        .getPublisherInfoByUserId(tenant.getContactUserId());
                if (publisherInfo != null) {
                    publisherInfo.setIdentityAuthStatus(2); // 已认证
                    publisherInfoService.updatePublisherInfo(BeanUtils.toBean(publisherInfo,
                            cc.bamboo.module.system.controller.admin.publisherinfo.vo.PublisherInfoSaveReqVO.class));
                }
            });
        }
    }

    @Override
    public TenantAuditStatusRespVO getAuditStatus() {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        TenantDO tenant = tenantMapper.selectById(tenantId);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }

        TenantAuditStatusRespVO respVO = new TenantAuditStatusRespVO();
        respVO.setAuditStatus(tenant.getAuditStatus() != null ? tenant.getAuditStatus() : 0);
        respVO.setAuditRemark(tenant.getAuditRemark());

        // 检查是否已有发行商信息
        TenantUtils.execute(tenantId, () -> {
            PublisherInfoDO publisherInfo = publisherInfoService.getPublisherInfoByUserId(tenant.getContactUserId());
            respVO.setHasPublisherInfo(publisherInfo != null);
        });

        return respVO;
    }

    @Override
    public List<TenantDO> getTenantList() {
        return tenantMapper.selectList();
    }

    @Override
    public TenantSubmitAuditReqVO getPublisherInfo() {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        TenantDO tenant = tenantMapper.selectById(tenantId);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }

        // 获取发行商信息
        PublisherInfoDO publisherInfo = publisherInfoService.getPublisherInfoByUserId(tenant.getContactUserId());
        if (publisherInfo == null) {
            return null; // 没有发行商信息返回null
        }

        // 转换为VO
        TenantSubmitAuditReqVO vo = new TenantSubmitAuditReqVO();
        vo.setCompanyCreditCode(publisherInfo.getCompanyCreditCode());
        vo.setBusinessLicenseUrl(publisherInfo.getBusinessLicenseUrl());
        vo.setQualificationFileUrls(publisherInfo.getQualificationFileUrls());
        vo.setAuthorizationFileUrls(publisherInfo.getAuthorizationFileUrls());
        vo.setAuthIdentity(publisherInfo.getAuthIdentity());
        vo.setIdCardName(publisherInfo.getIdCardName());
        vo.setIdCardNo(publisherInfo.getIdCardNo());
        vo.setIdCardExpireTime(publisherInfo.getIdCardExpireTime());
        vo.setIdCardFrontUrl(publisherInfo.getIdCardFrontUrl());
        vo.setIdCardBackUrl(publisherInfo.getIdCardBackUrl());
        vo.setEmail(publisherInfo.getEmail());
        vo.setBankAccountName(publisherInfo.getBankAccountName());
        vo.setBankAccount(publisherInfo.getBankAccount());
        vo.setBankName(publisherInfo.getBankName());

        return vo;
    }

    @Override
    public TenantSubmitAuditReqVO getPublisherInfoByTenantId(Long tenantId) {
        TenantDO tenant = tenantMapper.selectById(tenantId);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }

        // 获取发行商信息（使用租户上下文）
        final TenantSubmitAuditReqVO[] result = new TenantSubmitAuditReqVO[1];
        TenantUtils.execute(tenantId, () -> {
            PublisherInfoDO publisherInfo = publisherInfoService.getPublisherInfoByUserId(tenant.getContactUserId());
            if (publisherInfo == null) {
                result[0] = null;
                return;
            }

            // 转换为VO
            TenantSubmitAuditReqVO vo = new TenantSubmitAuditReqVO();
            vo.setCompanyCreditCode(publisherInfo.getCompanyCreditCode());
            vo.setBusinessLicenseUrl(publisherInfo.getBusinessLicenseUrl());
            vo.setQualificationFileUrls(publisherInfo.getQualificationFileUrls());
            vo.setAuthorizationFileUrls(publisherInfo.getAuthorizationFileUrls());
            vo.setAuthIdentity(publisherInfo.getAuthIdentity());
            vo.setIdCardName(publisherInfo.getIdCardName());
            vo.setIdCardNo(publisherInfo.getIdCardNo());
            vo.setIdCardExpireTime(publisherInfo.getIdCardExpireTime());
            vo.setIdCardFrontUrl(publisherInfo.getIdCardFrontUrl());
            vo.setIdCardBackUrl(publisherInfo.getIdCardBackUrl());
            vo.setEmail(publisherInfo.getEmail());
            vo.setBankAccountName(publisherInfo.getBankAccountName());
            vo.setBankAccount(publisherInfo.getBankAccount());
            vo.setBankName(publisherInfo.getBankName());
            result[0] = vo;
        });

        return result[0];
    }

}
