package cc.bamboo.module.user.service.useraudit;

import cc.bamboo.framework.common.exception.ServiceException;
import cc.bamboo.module.user.dal.dataobject.userbank.UserBankDO;
import cc.bamboo.module.user.dal.mysql.userbank.UserBankMapper;
import cc.bamboo.module.user.enums.UserAuditStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.user.controller.admin.useraudit.vo.*;
import cc.bamboo.module.user.dal.dataobject.useraudit.UserAuditDO;
import cc.bamboo.module.user.dal.dataobject.userinfo.UserInfoDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.user.dal.mysql.useraudit.UserAuditMapper;
import cc.bamboo.module.user.dal.mysql.userinfo.UserInfoMapper;
import cc.bamboo.module.user.service.userinfo.UserInfoService;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.*;

/**
 * 用户投资者认证审核 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class UserAuditServiceImpl implements UserAuditService {

    @Resource
    private UserAuditMapper auditMapper;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserBankMapper userBankMapper;

    @Override
    public Long createAudit(UserAuditSaveReqVO createReqVO) {
        // 插入
        UserAuditDO audit = BeanUtils.toBean(createReqVO, UserAuditDO.class);
        auditMapper.insert(audit);
        // 返回
        return audit.getId();
    }

    @Override
    public void updateAudit(UserAuditSaveReqVO updateReqVO) {
        // 校验存在
        validateAuditExists(updateReqVO.getId());
        // 更新
        UserAuditDO updateObj = BeanUtils.toBean(updateReqVO, UserAuditDO.class);
        auditMapper.updateById(updateObj);
    }

    @Override
    public void deleteAudit(Long id) {
        // 校验存在
        validateAuditExists(id);
        // 删除
        auditMapper.deleteById(id);
    }

    private void validateAuditExists(Long id) {
        if (auditMapper.selectById(id) == null) {
            throw exception(AUDIT_NOT_EXISTS);
        }
    }

    @Override
    public UserAuditDO getAudit(Long id) {
        return auditMapper.selectById(id);
    }

    @Override
    public PageResult<UserAuditDO> getAuditPage(UserAuditPageReqVO pageReqVO) {
        return auditMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long reviewAudit(UserAuditReviewReqVO reviewReqVO) {

        if(Objects.equals(reviewReqVO.getAuditStatus(), UserAuditStatusEnum.REJECTED.getStatus())
        && StringUtils.isBlank(reviewReqVO.getAuditRemark())){
            throw exception(AUDIT_NO_REMARK);
        }
        // 1. 校验审核记录是否存在
        UserAuditDO audit = auditMapper.selectById(reviewReqVO.getId());
        if (audit == null) {
            throw exception(AUDIT_NOT_EXISTS);
        }

        // 2. 校验审核状态是否合法（只能是2-通过或3-驳回）
        if (!Objects.equals(reviewReqVO.getAuditStatus(), UserAuditStatusEnum.APPROVED.getStatus()) && !Objects.equals(reviewReqVO.getAuditStatus(), UserAuditStatusEnum.REJECTED.getStatus())) {
            throw exception(new cc.bamboo.framework.common.exception.ErrorCode(10003, "审核状态不合法"));
        }

        // 3. 更新审核记录
        UserAuditDO updateObj = new UserAuditDO();
        updateObj.setId(reviewReqVO.getId());
        updateObj.setAuditStatus(reviewReqVO.getAuditStatus());
        updateObj.setAuditRemark(reviewReqVO.getAuditRemark());
        auditMapper.updateById(updateObj);

        // 4. 如果审核通过，同步更新用户信息表
        if (Objects.equals(reviewReqVO.getAuditStatus(), UserAuditStatusEnum.APPROVED.getStatus())) {
            UserInfoDO userInfo = userInfoService.getInfo(audit.getUserId());
            if (userInfo != null) {
                UserInfoDO updateUserInfo = new UserInfoDO();
                updateUserInfo.setId(audit.getUserId());
                updateUserInfo.setAuditStatus(UserAuditStatusEnum.APPROVED.getStatus()); // 审核通过
                updateUserInfo.setRealName(audit.getRealName());
                updateUserInfo.setIdCard(audit.getIdCard());
                updateUserInfo.setIdCardExpire(audit.getIdCardExpire());
                updateUserInfo.setPhone(audit.getContactPhone());
                userInfoMapper.updateById(updateUserInfo);
            }
            //更新银行卡审核状态
            UserBankDO userBankDO = new UserBankDO();
            userBankDO.setId(audit.getBankCardId());
            userBankDO.setAuditStatus(UserAuditStatusEnum.APPROVED.getStatus()); // 审核通过
            userBankMapper.updateById(userBankDO);
        }else{

            UserInfoDO updateUserInfo = new UserInfoDO();
            updateUserInfo.setId(audit.getUserId());
            updateUserInfo.setAuditStatus(UserAuditStatusEnum.REJECTED.getStatus()); // 审核通过
            userInfoMapper.updateById(updateUserInfo);
            //更新银行卡审核状态
            UserBankDO userBankDO = new UserBankDO();
            userBankDO.setId(audit.getBankCardId());
            userBankDO.setAuditStatus(UserAuditStatusEnum.REJECTED.getStatus()); // 审核通过
            userBankMapper.updateById(userBankDO);
        }
        return audit.getUserId();
    }

}