package cc.bamboo.module.user.dal.mysql.useraudit;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.user.dal.dataobject.useraudit.UserAuditDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.user.controller.admin.useraudit.vo.*;

/**
 * 用户投资者认证审核 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface UserAuditMapper extends BaseMapperX<UserAuditDO> {

    default PageResult<UserAuditDO> selectPage(UserAuditPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserAuditDO>()
                .eqIfPresent(UserAuditDO::getUserId, reqVO.getUserId())
                .likeIfPresent(UserAuditDO::getRealName, reqVO.getRealName())
                .eqIfPresent(UserAuditDO::getIdCard, reqVO.getIdCard())
                .eqIfPresent(UserAuditDO::getIdCardExpire, reqVO.getIdCardExpire())
                .eqIfPresent(UserAuditDO::getIdCardFrontUrl, reqVO.getIdCardFrontUrl())
                .eqIfPresent(UserAuditDO::getIdCardBackUrl, reqVO.getIdCardBackUrl())
                .eqIfPresent(UserAuditDO::getInvestmentQualificationUrl, reqVO.getInvestmentQualificationUrl())
                .eqIfPresent(UserAuditDO::getBankFlowUrl, reqVO.getBankFlowUrl())
                .eqIfPresent(UserAuditDO::getResidenceProofUrl, reqVO.getResidenceProofUrl())
                .eqIfPresent(UserAuditDO::getBankCardId, reqVO.getBankCardId())
                .eqIfPresent(UserAuditDO::getEmail, reqVO.getEmail())
                .eqIfPresent(UserAuditDO::getContactPhone, reqVO.getContactPhone())
                .eqIfPresent(UserAuditDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(UserAuditDO::getSubmitVersion, reqVO.getSubmitVersion())
                .eqIfPresent(UserAuditDO::getAuditRemark, reqVO.getAuditRemark())
                .eqIfPresent(UserAuditDO::getIsLatest, reqVO.getIsLatest())
                .betweenIfPresent(UserAuditDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserAuditDO::getId));
    }

}