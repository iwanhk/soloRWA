package cc.bamboo.module.project.dal.mysql.projectorderbalance;

import java.math.BigDecimal;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.controller.app.orderbalance.vo.AppAssetByCurrencyRespVO;
import cc.bamboo.module.project.controller.app.orderbalance.vo.AppAssetDetailRespVO;
import cc.bamboo.module.project.controller.app.orderbalance.vo.AppProjectOrderBalancePageReqVO;
import cc.bamboo.module.project.dal.dataobject.projectorderbalance.ProjectOrderBalanceDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import cc.bamboo.module.project.controller.admin.projectorderbalance.vo.*;

import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户项目余额表 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ProjectOrderBalanceMapper extends BaseMapperX<ProjectOrderBalanceDO> {

        /**
         * 查询用户的总收益
         * 
         * @param userId    用户ID
         * @param projectId 项目ID (可选)
         * @param orderId   订单ID (可选)
         * @return 总收益金额
         */
        @Select("<script>" +

                        "SELECT earning_currency as coinCode, " +
                        "       IFNULL(SUM(total_income), 0.00) as total " +
                        "FROM biz_project_order_balance " +
                        "WHERE user_id = #{userId} " +
                        "AND deleted = 0 " +
                        "<if test='projectId != null'> AND project_id = #{projectId} </if>" +
                        "<if test='orderId != null'> AND order_id = #{orderId} </if>" +
                        "GROUP BY earning_currency" +
                        "</script>")
        List<AppAssetByCurrencyRespVO> selectTotalIncomeByUserId(@Param("userId") Long userId,
                        @Param("projectId") Long projectId,
                        @Param("orderId") Long orderId);

        /**
         * 按投资币种分组查询用户的总资产
         * 
         * @param userId    用户ID
         * @param projectId 项目ID (可选)
         * @param orderId   订单ID (可选)
         * @return 按币种分组的资产列表
         */
        @Select("<script>" +
                        "SELECT investment_currency as coinCode, " +
                        "       IFNULL(SUM(hold_amount), 0.00) as total " +
                        "FROM biz_project_order_balance " +
                        "WHERE user_id = #{userId} " +
                        "AND deleted = 0 " +
                        "<if test='projectId != null'> AND project_id = #{projectId} </if>" +
                        "<if test='orderId != null'> AND order_id = #{orderId} </if>" +
                        "GROUP BY investment_currency" +
                        "</script>")
        List<AppAssetByCurrencyRespVO> selectTotalAssetsByUserIdGroupByCurrency(@Param("userId") Long userId,
                        @Param("projectId") Long projectId,
                        @Param("orderId") Long orderId);


        default PageResult<ProjectOrderBalanceDO> selectPage(ProjectOrderBalancePageReqVO reqVO) {
                return selectPage(reqVO, new LambdaQueryWrapperX<ProjectOrderBalanceDO>()
                                .eqIfPresent(ProjectOrderBalanceDO::getUserId, reqVO.getUserId())
                                .eqIfPresent(ProjectOrderBalanceDO::getProjectId, reqVO.getProjectId())
                                .eqIfPresent(ProjectOrderBalanceDO::getOrderId, reqVO.getOrderId())
                                .eqIfPresent(ProjectOrderBalanceDO::getPrincipalAmount, reqVO.getPrincipalAmount())
                                .eqIfPresent(ProjectOrderBalanceDO::getHoldAmount, reqVO.getHoldAmount())
                                .eqIfPresent(ProjectOrderBalanceDO::getBuyQuantity, reqVO.getBuyQuantity())
                                .eqIfPresent(ProjectOrderBalanceDO::getHoldQuantity, reqVO.getHoldQuantity())
                                .eqIfPresent(ProjectOrderBalanceDO::getTotalIncome, reqVO.getTotalIncome())
                                .eqIfPresent(ProjectOrderBalanceDO::getWithdrawnDividend, reqVO.getWithdrawnDividend())
                                .eqIfPresent(ProjectOrderBalanceDO::getFreezeDividend, reqVO.getFreezeDividend())
                                .eqIfPresent(ProjectOrderBalanceDO::getUnwithdrawnDividend,
                                                reqVO.getUnwithdrawnDividend())
                                .eqIfPresent(ProjectOrderBalanceDO::getTotalRedemptionAmount,
                                                reqVO.getTotalRedemptionAmount())
                                .betweenIfPresent(ProjectOrderBalanceDO::getLastIncomeCalcTime,
                                                reqVO.getLastIncomeCalcTime())
                                .betweenIfPresent(ProjectOrderBalanceDO::getLastWithdrawTime,
                                                reqVO.getLastWithdrawTime())
                                .betweenIfPresent(ProjectOrderBalanceDO::getCreateTime, reqVO.getCreateTime())
                                .orderByDesc(ProjectOrderBalanceDO::getId));
        }

        default PageResult<ProjectOrderBalanceDO> selectPage(AppProjectOrderBalancePageReqVO reqVO) {
                return selectPage(reqVO, new LambdaQueryWrapperX<ProjectOrderBalanceDO>()
                                .eqIfPresent(ProjectOrderBalanceDO::getUserId, reqVO.getUserId())
                                .eqIfPresent(ProjectOrderBalanceDO::getProjectId, reqVO.getProjectId())
                                .orderByDesc(ProjectOrderBalanceDO::getId));
        }

        /**
         * 使用乐观锁更新余额（提前赎回）
         * 
         * @param balanceId           余额ID
         * @param redemptionQuantity  赎回份额
         * @param redemptionPrincipal 赎回本金
         * @return 更新行数（0表示余额不足，1表示更新成功）
         */
        @Update("UPDATE biz_project_order_balance " +
                        "SET hold_quantity = hold_quantity - #{redemptionQuantity}, " +
                        "    hold_amount = hold_amount - #{redemptionPrincipal}, " +
                        "    total_redemption_amount = total_redemption_amount + #{redemptionPrincipal}, " +
                        "    updater = #{updater}, " +
                        "    update_time = NOW() " +
                        "WHERE id = #{balanceId} " +
                        "  AND hold_quantity >= #{redemptionQuantity} " +
                        "  AND hold_amount >= #{redemptionPrincipal} " +
                        "  AND deleted = 0")
        int updateBalanceForEarlyRedemption(@Param("balanceId") Long balanceId,
                        @Param("redemptionQuantity") Integer redemptionQuantity,
                        @Param("redemptionPrincipal") BigDecimal redemptionPrincipal,
                        @Param("updater") String updater);

        /**
         * 使用乐观锁更新余额（分红申请）
         * 
         * @param balanceId      余额ID
         * @param dividendAmount 分红金额
         * @return 更新行数（0表示余额不足，1表示更新成功）
         */
        @Update("UPDATE biz_project_order_balance " +
                        "SET withdrawn_dividend = withdrawn_dividend + #{dividendAmount}, " +
                        "    unwithdrawn_dividend = unwithdrawn_dividend - #{dividendAmount}, " +
                        "    last_withdraw_time = NOW(), " +
                        "    updater = #{updater}, " +
                        "    update_time = NOW() " +
                        "WHERE id = #{balanceId} " +
                        "  AND unwithdrawn_dividend >= #{dividendAmount} " +
                        "  AND deleted = 0")
        int updateBalanceForDividend(@Param("balanceId") Long balanceId,
                        @Param("dividendAmount") BigDecimal dividendAmount,
                        @Param("updater") String updater);

        /**
         * 使用乐观锁更新余额（到期赎回）
         * 
         * @param balanceId        余额ID
         * @param redemptionAmount 赎回金额
         * @return 更新行数（0表示余额不足，1表示更新成功）
         */
        @Update("UPDATE biz_project_order_balance " +
                        "SET hold_quantity = 0, " +
                        "    hold_amount = 0, " +
                        "    total_redemption_amount = total_redemption_amount + #{redemptionAmount}, " +
                        "    updater = #{updater}, " +
                        "    update_time = NOW() " +
                        "WHERE id = #{balanceId} " +
                        "  AND hold_amount >= #{redemptionAmount} " +
                        "  AND deleted = 0")
        int updateBalanceForMaturityRedemption(@Param("balanceId") Long balanceId,
                        @Param("redemptionAmount") BigDecimal redemptionAmount,
                        @Param("updater") String updater);

        /**
         * 退还分红（审核不通过）
         * 
         * @param balanceId    余额ID
         * @param refundAmount 退还金额
         * @return 更新行数
         */
        @Update("UPDATE biz_project_order_balance " +
                        "SET withdrawn_dividend = withdrawn_dividend - #{refundAmount}, " +
                        "    unwithdrawn_dividend = unwithdrawn_dividend + #{refundAmount}, " +
                        "    update_time = NOW() " +
                        "WHERE id = #{balanceId} " +
                        "  AND deleted = 0")
        int refundDividend(@Param("balanceId") Long balanceId,
                        @Param("refundAmount") BigDecimal refundAmount);

        /**
         * 退还赎回（审核不通过）
         * 
         * @param balanceId      余额ID
         * @param refundQuantity 退还份额
         * @param refundAmount   退还金额
         * @return 更新行数
         */
        @Update("UPDATE biz_project_order_balance " +
                        "SET hold_quantity = hold_quantity + #{refundQuantity}, " +
                        "    hold_amount = hold_amount + #{refundAmount}, " +
                        "    total_redemption_amount = total_redemption_amount - #{refundAmount}, " +
                        "    update_time = NOW() " +
                        "WHERE id = #{balanceId} " +
                        "  AND deleted = 0")
        int refundRedemption(@Param("balanceId") Long balanceId,
                        @Param("refundQuantity") Integer refundQuantity,
                        @Param("refundAmount") BigDecimal refundAmount);

        default ProjectOrderBalanceDO getProjectOrderBalance(Long orderId, Long userId) {
                return selectOne(new LambdaQueryWrapper<ProjectOrderBalanceDO>()
                                .eq(ProjectOrderBalanceDO::getOrderId, orderId)
                                .eq(ProjectOrderBalanceDO::getUserId, userId));
        }

        /**
         * 原子更新订单余额（直接在SQL中累加，解决并发问题）
         * 
         * @param id               余额记录ID
         * @param orderDailyIncome 要累加的每日收益
         * @return 影响的行数（1=更新成功，0=无匹配记录）
         */
        @Update("UPDATE biz_project_order_balance " +
                        "SET total_income = total_income + #{orderDailyIncome}, " +
                        "    unwithdrawn_dividend = unwithdrawn_dividend + #{orderDailyIncome} " +
                        "WHERE id = #{id} " +
                        "  AND deleted = 0") // 保留你的逻辑删除条件
        int updateBalanceAddIncome(@Param("id") Long id,
                        @Param("orderDailyIncome") BigDecimal orderDailyIncome);

        default List<ProjectOrderBalanceDO> getProjectOrderBalanceList(Set<Long> orderIds) {
                return selectList(new LambdaQueryWrapper<ProjectOrderBalanceDO>()
                                .in(ProjectOrderBalanceDO::getOrderId, orderIds));
        }
}