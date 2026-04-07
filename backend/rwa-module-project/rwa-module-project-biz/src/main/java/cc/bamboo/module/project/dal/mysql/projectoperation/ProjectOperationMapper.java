package cc.bamboo.module.project.dal.mysql.projectoperation;

import java.math.BigDecimal;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.dal.dataobject.projectoperation.ProjectOperationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import cc.bamboo.module.project.controller.admin.projectoperation.vo.*;

/**
 * 项目运营统计表 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ProjectOperationMapper extends BaseMapperX<ProjectOperationDO> {

    default PageResult<ProjectOperationDO> selectPage(ProjectOperationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProjectOperationDO>()
                .eqIfPresent(ProjectOperationDO::getInvestorCount, reqVO.getInvestorCount())
                .eqIfPresent(ProjectOperationDO::getDividendApplyCount, reqVO.getDividendApplyCount())
                .eqIfPresent(ProjectOperationDO::getDividendApplyAmount, reqVO.getDividendApplyAmount())
                .eqIfPresent(ProjectOperationDO::getEarlyRedemptionPeople, reqVO.getEarlyRedemptionPeople())
                .eqIfPresent(ProjectOperationDO::getEarlyRedemptionAmount, reqVO.getEarlyRedemptionAmount())
                .eqIfPresent(ProjectOperationDO::getEarlyRedemptionCount, reqVO.getEarlyRedemptionCount())
                .eqIfPresent(ProjectOperationDO::getMaturityRedemptionCount, reqVO.getMaturityRedemptionCount())
                .eqIfPresent(ProjectOperationDO::getMaturityRedemptionAmount, reqVO.getMaturityRedemptionAmount())
                .eqIfPresent(ProjectOperationDO::getTotalInvestorIncome, reqVO.getTotalInvestorIncome())
                .eqIfPresent(ProjectOperationDO::getTotalInvestorYield, reqVO.getTotalInvestorYield())
                .betweenIfPresent(ProjectOperationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProjectOperationDO::getProjectId));
    }

    /**
     * 原子性更新提前赎回统计
     * 
     * @param projectId 项目ID
     * @param quantity 赎回份额
     * @param amount 赎回金额
     * @return 更新行数
     */
    @Update("UPDATE biz_project_operation " +
            "SET early_redemption_people = early_redemption_people + 1, " +
            "    early_redemption_amount = early_redemption_amount + #{amount}, " +
            "    early_redemption_count = early_redemption_count + #{quantity}, " +
            "    update_time = NOW() " +
            "WHERE project_id = #{projectId} " +
            "  AND deleted = 0")
    int incrementEarlyRedemption(@Param("projectId") Long projectId,
                                  @Param("quantity") Integer quantity,
                                  @Param("amount") BigDecimal amount);

    /**
     * 原子性更新分红统计
     * 
     * @param projectId 项目ID
     * @param amount 分红金额
     * @return 更新行数
     */
    @Update("UPDATE biz_project_operation " +
            "SET dividend_apply_count = dividend_apply_count + 1, " +
            "    dividend_apply_amount = dividend_apply_amount + #{amount}, " +
            "    update_time = NOW() " +
            "WHERE project_id = #{projectId} " +
            "  AND deleted = 0")
    int incrementDividend(@Param("projectId") Long projectId,
                          @Param("amount") BigDecimal amount);

    /**
     * 原子性更新到期赎回统计
     * 
     * @param projectId 项目ID
     * @param amount 赎回金额
     * @return 更新行数
     */
    @Update("UPDATE biz_project_operation " +
            "SET maturity_redemption_count = maturity_redemption_count + 1, " +
            "    maturity_redemption_amount = maturity_redemption_amount + #{amount}, " +
            "    update_time = NOW() " +
            "WHERE project_id = #{projectId} " +
            "  AND deleted = 0")
    int incrementMaturityRedemption(@Param("projectId") Long projectId,
                                     @Param("amount") BigDecimal amount);



    /**
     * 原子性更新投资人总收益
     * 
     * @param projectId 项目ID
     * @param amount 收益金额
     * @return 更新行数
     */
    @Update("UPDATE biz_project_operation " +
            "SET total_investor_income = total_investor_income + #{amount}, " +
            "    update_time = NOW() " +
            "WHERE project_id = #{projectId} " +
            "  AND deleted = 0")
    int incrementTotalInvestorIncome(@Param("projectId") Long projectId,
                                      @Param("amount") BigDecimal amount);

    @Update("UPDATE biz_project_operation " +
            "SET project_income = project_income + #{amount}, " +
            "    update_time = NOW() " +
            "WHERE project_id = #{projectId} " +
            "  AND deleted = 0")
    int incrementProjectIncome(@Param("projectId") Long projectId,
                                     @Param("amount") BigDecimal amount);
}