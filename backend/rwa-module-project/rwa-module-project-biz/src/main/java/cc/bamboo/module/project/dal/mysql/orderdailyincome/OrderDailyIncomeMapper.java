package cc.bamboo.module.project.dal.mysql.orderdailyincome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.controller.app.orderbalance.vo.AppAssetByCurrencyRespVO;
import cc.bamboo.module.project.controller.app.orderbalance.vo.AppIncomeByCoinRespVO;
import cc.bamboo.module.project.controller.app.orderbalance.vo.AppIncomeCalendarRespVO;
import cc.bamboo.module.project.dal.dataobject.orderdailyincome.OrderDailyIncomeDO;
import cc.bamboo.module.project.enums.SendStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.project.controller.admin.orderdailyincome.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 订单每日收益统计 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface OrderDailyIncomeMapper extends BaseMapperX<OrderDailyIncomeDO> {

        default PageResult<OrderDailyIncomeDO> selectPage(OrderDailyIncomePageReqVO reqVO) {
                return selectPage(reqVO, new LambdaQueryWrapperX<OrderDailyIncomeDO>()
                                .eqIfPresent(OrderDailyIncomeDO::getOrderId, reqVO.getOrderId())
                                .eqIfPresent(OrderDailyIncomeDO::getOrderNo, reqVO.getOrderNo())
                                .eqIfPresent(OrderDailyIncomeDO::getUserId, reqVO.getUserId())
                                .eqIfPresent(OrderDailyIncomeDO::getProjectId, reqVO.getProjectId())
                                .betweenIfPresent(OrderDailyIncomeDO::getIncomeDate, reqVO.getIncomeDate())
                                .eqIfPresent(OrderDailyIncomeDO::getProjectRevenueId, reqVO.getProjectRevenueId())
                                .eqIfPresent(OrderDailyIncomeDO::getHoldQuantity, reqVO.getHoldQuantity())
                                .betweenIfPresent(OrderDailyIncomeDO::getIssueTime, reqVO.getIssueTime())
                                .eqIfPresent(OrderDailyIncomeDO::getDailyIncome, reqVO.getDailyIncome())
                                .eqIfPresent(OrderDailyIncomeDO::getIncomeRate, reqVO.getIncomeRate())
                                .eqIfPresent(OrderDailyIncomeDO::getCumulativeIncome, reqVO.getCumulativeIncome())
                                .eqIfPresent(OrderDailyIncomeDO::getStatus, reqVO.getStatus())
                                .betweenIfPresent(OrderDailyIncomeDO::getCreateTime, reqVO.getCreateTime())
                                .orderByDesc(OrderDailyIncomeDO::getId));
        }

        @Select("<script>" +
                        "SELECT IFNULL(SUM(daily_income), 0.000000) as totalIncome, " +
                        "       coin_code as coinCode " +
                        "FROM biz_order_daily_income " +
                        "WHERE income_date = #{date} " +
                        "AND user_id = #{userId} " +
                        "AND deleted = 0 " +
                        "<if test='projectId != null'> AND project_id = #{projectId} </if>" +
                        "<if test='orderId != null'> AND order_id = #{orderId} </if>" +
                        "GROUP BY coin_code" +
                        "</script>")
        List<AppAssetByCurrencyRespVO> selectSumIncomeByDateAndUserId(@Param("date") LocalDate date,
                        @Param("userId") Long userId,
                        @Param("projectId") Long projectId,
                        @Param("orderId") Long orderId);

        default List<OrderDailyIncomeDO> selectListByDateAndUserId(LocalDate date, Long userId, Long projectId,
                        Long orderId) {
                return selectList(new LambdaQueryWrapperX<OrderDailyIncomeDO>()
                                .eq(OrderDailyIncomeDO::getIncomeDate, date)
                                .eq(OrderDailyIncomeDO::getUserId, userId)
                                .eqIfPresent(OrderDailyIncomeDO::getProjectId, projectId)
                                .eqIfPresent(OrderDailyIncomeDO::getOrderId, orderId)
                                .orderByDesc(OrderDailyIncomeDO::getId));
        }

        @Select("<script>" +
                "SELECT income_date AS incomeDate, coin_code as coinCode, IFNULL(SUM(daily_income), 0.000000) AS total " +
                "FROM biz_order_daily_income " +
                "WHERE user_id = #{userId} " +
                "AND income_date BETWEEN #{startDate} AND #{endDate} " +
                "AND deleted = 0 " +
                "<if test='projectId != null'> AND project_id = #{projectId} </if>" +
                "<if test='orderId != null'> AND order_id = #{orderId} </if>" +
                "GROUP BY income_date, coin_code " +
                "ORDER BY income_date ASC, coin_code ASC" +
                "</script>")
        List<AppIncomeByCoinRespVO> selectDailySumListByDateRangeAndUserId(
                @Param("startDate") LocalDate startDate,
                @Param("endDate") LocalDate endDate,
                @Param("userId") Long userId,
                @Param("projectId") Long projectId,
                @Param("orderId") Long orderId);

        /**
         * 批量插入订单每日收益
         *
         * @param list 订单每日收益列表
         * @return 插入行数
         */
        default int insertBatch(List<OrderDailyIncomeDO> list) {
                if (list == null || list.isEmpty()) {
                        return 0;
                }
                list.forEach(this::insert);
                return list.size();
        }

        /**
         * 批量更新为已发放状态
         *
         * @param projectRevenueId 项目收益ID
         * @return 更新行数
         */
        @Update("UPDATE biz_order_daily_income SET is_sent = 1, update_time = NOW() " +
                        "WHERE project_revenue_id = #{projectRevenueId} AND is_sent = 0 AND deleted = 0")
        int batchUpdateToSent(@Param("projectRevenueId") Long projectRevenueId);

        /**
         * 查询未发放的订单收益
         *
         * @param projectRevenueId 项目收益ID
         * @return 订单收益列表
         */
        default List<OrderDailyIncomeDO> selectUnsentByProjectRevenueId(Long projectRevenueId) {
                LambdaQueryWrapper<OrderDailyIncomeDO> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(projectRevenueId != null, OrderDailyIncomeDO::getProjectRevenueId, projectRevenueId);
                // wrapper.eq(OrderDailyIncomeDO::getIncomeDate, date);
                wrapper.eq(OrderDailyIncomeDO::getStatus, SendStatusEnum.NOT_SEND.getStatus());
                return selectList(wrapper);
        }

        /**
         * 计算订单在指定日期之前的已发放收益总和
         * 用于计算可提现余额
         */
        @Select("SELECT IFNULL(SUM(daily_income), 0.00) FROM biz_order_daily_income " +
                        "WHERE order_id = #{orderId} AND income_date < #{unlockDate} AND status = 1 AND deleted = 0")
        BigDecimal selectSumIncomeBeforeDate(@Param("orderId") Long orderId, @Param("unlockDate") LocalDate unlockDate);
}