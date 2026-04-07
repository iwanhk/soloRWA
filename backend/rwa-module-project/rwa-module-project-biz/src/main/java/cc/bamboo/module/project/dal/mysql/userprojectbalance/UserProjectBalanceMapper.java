package cc.bamboo.module.project.dal.mysql.userprojectbalance;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.dal.dataobject.userprojectbalance.UserProjectBalanceDO;
import cc.bamboo.module.project.dal.mysql.userprojectbalance.vo.ProjectIncomeAggRespDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cc.bamboo.module.project.controller.admin.userprojectbalance.vo.*;

/**
 * 用户项目余额表（本金/收益汇总） Mapper
 *
 * @author Swolf
 */
@Mapper
public interface UserProjectBalanceMapper extends BaseMapperX<UserProjectBalanceDO> {

    default PageResult<UserProjectBalanceDO> selectPage(UserProjectBalancePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserProjectBalanceDO>()
                .eqIfPresent(UserProjectBalanceDO::getUserId, reqVO.getUserId())
                .eqIfPresent(UserProjectBalanceDO::getProjectId, reqVO.getProjectId())
                .eqIfPresent(UserProjectBalanceDO::getPrincipalAmount, reqVO.getPrincipalAmount())
                .eqIfPresent(UserProjectBalanceDO::getHoldQuantity, reqVO.getHoldQuantity())
                .eqIfPresent(UserProjectBalanceDO::getTotalIncome, reqVO.getTotalIncome())
                .eqIfPresent(UserProjectBalanceDO::getWithdrawnDividend, reqVO.getWithdrawnDividend())
                .eqIfPresent(UserProjectBalanceDO::getUnwithdrawnDividend, reqVO.getUnwithdrawnDividend())
                .eqIfPresent(UserProjectBalanceDO::getTotalRedemptionAmount, reqVO.getTotalRedemptionAmount())
                .betweenIfPresent(UserProjectBalanceDO::getLastIncomeCalcTime, reqVO.getLastIncomeCalcTime())
                .betweenIfPresent(UserProjectBalanceDO::getLastWithdrawTime, reqVO.getLastWithdrawTime())
                .eqIfPresent(UserProjectBalanceDO::getBalanceStatus, reqVO.getBalanceStatus())
                .betweenIfPresent(UserProjectBalanceDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserProjectBalanceDO::getId));
    }

    @Select({
            "<script>",
            "SELECT project_id AS projectId,",
            "COALESCE(SUM(total_income), 0) AS totalIncome,",
            "COALESCE(SUM(withdrawn_dividend), 0) AS withdrawnDividend",
            "FROM biz_user_project_balance",
            "WHERE deleted = 0",
            "AND project_id IN",
            "<foreach collection='projectIds' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "GROUP BY project_id",
            "</script>"
    })
    List<ProjectIncomeAggRespDTO> selectIncomeAggByProjectIds(@Param("projectIds") List<Long> projectIds);

}
