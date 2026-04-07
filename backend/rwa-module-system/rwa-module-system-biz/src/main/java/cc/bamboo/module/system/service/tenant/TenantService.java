package cc.bamboo.module.system.service.tenant;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.tenant.core.context.TenantContextHolder;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantSaveReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantCreateWithAuditReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantSubmitAuditReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantAuditReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantAuditStatusRespVO;
import cc.bamboo.module.system.dal.dataobject.tenant.TenantDO;
import cc.bamboo.module.system.service.tenant.handler.TenantInfoHandler;
import cc.bamboo.module.system.service.tenant.handler.TenantMenuHandler;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * 租户 Service 接口
 *
 * @author 芋道源码
 */
public interface TenantService {

    /**
     * 创建租户
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTenant(@Valid TenantSaveReqVO createReqVO);

    /**
     * 创建租户（带审核状态）
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTenantWithAudit(@Valid TenantCreateWithAuditReqVO createReqVO);

    /**
     * 获取待审核数量
     *
     * @author: Hus
     * @date: 2026/2/5 18:58
     * @return: Long
     * @description
     */
    Long getAuditCount();

    /**
     * 更新租户
     *
     * @param updateReqVO 更新信息
     */
    void updateTenant(@Valid TenantSaveReqVO updateReqVO);

    /**
     * 更新租户的角色菜单
     *
     * @param tenantId 租户编号
     * @param menuIds  菜单编号数组
     */
    void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds);

    /**
     * 删除租户
     *
     * @param id 编号
     */
    void deleteTenant(Long id);

    /**
     * 获得租户
     *
     * @param id 编号
     * @return 租户
     */
    TenantDO getTenant(Long id);

    /**
     * 获得租户分页
     *
     * @param pageReqVO 分页查询
     * @return 租户分页
     */
    PageResult<TenantDO> getTenantPage(TenantPageReqVO pageReqVO);

    /**
     * 获得名字对应的租户
     *
     * @param name 租户名
     * @return 租户
     */
    TenantDO getTenantByName(String name);

    /**
     * 获得域名对应的租户
     *
     * @param website 域名
     * @return 租户
     */
    TenantDO getTenantByWebsite(String website);

    /**
     * 获得使用指定套餐的租户数量
     *
     * @param packageId 租户套餐编号
     * @return 租户数量
     */
    Long getTenantCountByPackageId(Long packageId);

    /**
     * 获得使用指定套餐的租户数组
     *
     * @param packageId 租户套餐编号
     * @return 租户数组
     */
    List<TenantDO> getTenantListByPackageId(Long packageId);

    /**
     * 进行租户的信息处理逻辑
     * 其中，租户编号从 {@link TenantContextHolder} 上下文中获取
     *
     * @param handler 处理器
     */
    void handleTenantInfo(TenantInfoHandler handler);

    /**
     * 进行租户的菜单处理逻辑
     * 其中，租户编号从 {@link TenantContextHolder} 上下文中获取
     *
     * @param handler 处理器
     */
    void handleTenantMenu(TenantMenuHandler handler);

    /**
     * 获得所有租户
     *
     * @return 租户编号数组
     */
    List<Long> getTenantIdList();

    /**
     * 校验租户是否合法
     *
     * @param id 租户编号
     */
    void validTenant(Long id);

    // ========== 审核相关方法 ==========

    /**
     * 提交审核
     * 开发商提交发行商信息进行审核
     *
     * @param reqVO 提交审核请求
     */
    void submitAudit(@Valid TenantSubmitAuditReqVO reqVO);

    /**
     * 审核租户
     * 超级管理员审核通过或拒绝
     *
     * @param reqVO 审核请求
     */
    void auditTenant(@Valid TenantAuditReqVO reqVO);

    /**
     * 获取当前租户审核状态
     *
     * @return 审核状态信息
     */
    TenantAuditStatusRespVO getAuditStatus();

    /**
     * 获得所有租户列表
     *
     * @return 租户列表
     */
    List<TenantDO> getTenantList();

    /**
     * 获取当前租户的发行商信息
     *
     * @return 发行商信息
     */
    TenantSubmitAuditReqVO getPublisherInfo();

    /**
     * 获取指定租户的发行商信息（管理员审核用）
     *
     * @param tenantId 租户ID
     * @return 发行商信息
     */
    TenantSubmitAuditReqVO getPublisherInfoByTenantId(Long tenantId);

}
