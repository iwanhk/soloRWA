package cc.bamboo.module.user.dal.mysql.userbank;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;

import cc.bamboo.module.user.dal.dataobject.userbank.UserBankDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import cc.bamboo.module.user.controller.admin.userbank.vo.*;

/**
 * 用户银行卡信息 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface UserBankMapper extends BaseMapperX<UserBankDO> {

    default PageResult<UserBankDO> selectPage(UserBankPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserBankDO>()
                .eqIfPresent(UserBankDO::getUserId, reqVO.getUserId())
                .likeIfPresent(UserBankDO::getBankAccountName, reqVO.getBankAccountName())
                .eqIfPresent(UserBankDO::getBankAccount, reqVO.getBankAccount())
                .likeIfPresent(UserBankDO::getBankName, reqVO.getBankName())
                .eqIfPresent(UserBankDO::getBankBranch, reqVO.getBankBranch())
                .eqIfPresent(UserBankDO::getIsDefault, reqVO.getIsDefault())
                .eqIfPresent(UserBankDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(UserBankDO::getAuditRemark, reqVO.getAuditRemark())
                .betweenIfPresent(UserBankDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserBankDO::getId));
    }

    /**
     * 根据用户ID和银行卡号查询
     */
    default UserBankDO selectByUserIdAndCardNo(Long userId, String bankCardNo) {
        return selectOne(new LambdaQueryWrapperX<UserBankDO>()
                .eq(UserBankDO::getUserId, userId)
                .eq(UserBankDO::getBankAccount, bankCardNo));
    }

    /**
     * 根据用户ID查询（获取第一张卡，通常用于单卡场景）
     */
    default UserBankDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<UserBankDO>()
                .eq(UserBankDO::getUserId, userId)
                .last("LIMIT 1"));
    }

    /**
     * 统计待审核的银行卡数量(仅统计已通过用户审核的)
     * 
     * @return 待审核银行卡数
     */
    @Select("SELECT COUNT(*) FROM biz_user_bank b " +
            "INNER JOIN biz_user_info u ON b.user_id = u.id " +
            "WHERE b.audit_status = 1 AND u.audit_status = 2 " +
            "AND b.deleted = 0 AND u.deleted = 0")
    Long countPendingAuditForApprovedUsers();

}