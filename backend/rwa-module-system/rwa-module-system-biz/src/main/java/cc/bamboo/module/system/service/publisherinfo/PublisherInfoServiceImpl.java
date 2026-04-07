package cc.bamboo.module.system.service.publisherinfo;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import cc.bamboo.module.system.controller.admin.publisherinfo.vo.*;
import cc.bamboo.module.system.dal.dataobject.publisherinfo.PublisherInfoDO;
import cc.bamboo.module.system.dal.dataobject.user.AdminUserDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.system.dal.mysql.publisherinfo.PublisherInfoMapper;
import cc.bamboo.module.system.dal.mysql.user.AdminUserMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.system.enums.ErrorCodeConstants.*;

/**
 * 发行商 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class PublisherInfoServiceImpl implements PublisherInfoService {

    @Resource
    private PublisherInfoMapper publisherInfoMapper;

    @Resource
    private AdminUserMapper adminUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPublisherInfo(PublisherInfoSaveReqVO createReqVO) {

        // 4. 创建发行商记录,使用用户ID作为发行商ID
        PublisherInfoDO publisherInfo = BeanUtils.toBean(createReqVO, PublisherInfoDO.class);
        publisherInfo.setRegisterTime(LocalDateTime.now());
        publisherInfoMapper.insert(publisherInfo);

        // 返回发行商ID(即用户ID)
        return publisherInfo.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePublisherInfo(PublisherInfoSaveReqVO updateReqVO) {
        // 1. 校验发行商存在
        PublisherInfoDO existingPublisher = publisherInfoMapper.selectById(updateReqVO.getId());
        if (existingPublisher == null) {
            throw exception(PUBLISHER_INFO_NOT_EXISTS);
        }

        // 2. 如果修改了统一社会信用代码,验证新代码是否已被其他发行商使用
        if (!existingPublisher.getCompanyCreditCode().equals(updateReqVO.getCompanyCreditCode())) {
            PublisherInfoDO duplicatePublisher = publisherInfoMapper.selectOne(
                    PublisherInfoDO::getCompanyCreditCode, updateReqVO.getCompanyCreditCode());
            if (duplicatePublisher != null) {
                throw exception(PUBLISHER_COMPANY_CREDIT_CODE_EXISTS);
            }
        }

        // 3. 更新发行商信息(手机号不可变,由前端控制)
        PublisherInfoDO updateObj = BeanUtils.toBean(updateReqVO, PublisherInfoDO.class);
        publisherInfoMapper.updateById(updateObj);

        // 4. 同步更新 system_users 表中的昵称(使用公司名称)
        AdminUserDO user = adminUserMapper.selectById(updateReqVO.getId());
        if (user != null && !user.getNickname().equals(updateReqVO.getCompanyName())) {
            user.setNickname(updateReqVO.getCompanyName());
            adminUserMapper.updateById(user);
        }
    }

    @Override
    public void deletePublisherInfo(Long id) {
        // 校验存在
        validatePublisherInfoExists(id);
        // 删除
        publisherInfoMapper.deleteById(id);
    }

    private void validatePublisherInfoExists(Long id) {
        if (publisherInfoMapper.selectById(id) == null) {
            throw exception(PUBLISHER_INFO_NOT_EXISTS);
        }
    }

    @Override
    public PublisherInfoDO getPublisherInfo(Long id) {
        return publisherInfoMapper.selectById(id);
    }

    @Override
    public PageResult<PublisherInfoDO> getPublisherInfoPage(PublisherInfoPageReqVO pageReqVO) {
        return publisherInfoMapper.selectPage(pageReqVO);
    }

    @Override
    public PublisherInfoDO getPublisherInfoByUserId(Long userId) {
        // 发行商ID与用户ID相同
        return publisherInfoMapper.selectById(userId);
    }

    @Override
    public PublisherInfoDO getPublisherInfoByTenantId(Long tenantId) {
        return publisherInfoMapper.selectOne(PublisherInfoDO::getTenantId, tenantId);
    }

}