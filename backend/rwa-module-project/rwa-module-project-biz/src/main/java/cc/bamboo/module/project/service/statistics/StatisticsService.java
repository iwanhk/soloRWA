package cc.bamboo.module.project.service.statistics;

import cc.bamboo.module.project.controller.admin.statistics.vo.*;

import java.util.List;

/**
 * 首页统计 Service 接口
 */
public interface StatisticsService {

    /**
     * 获取概览统计数据
     *
     * @return 概览统计
     */
    StatsOverviewRespVO getOverview();

    /**
     * 获取订单趋势（近30天）
     *
     * @return 订单趋势列表
     */
    List<OrderTrendRespVO> getOrderTrend();

    /**
     * 获取订单状态分布
     *
     * @return 订单状态分布列表
     */
    List<OrderStatusDistRespVO> getOrderStatusDist();

    /**
     * 获取热门项目排行
     *
     * @param limit 返回数量
     * @return 热门项目列表
     */
    List<TopProjectRespVO> getTopProjects(Integer limit);

    /**
     * 获取最新订单
     *
     * @param limit 返回数量
     * @return 最新订单列表
     */
    List<RecentOrderRespVO> getRecentOrders(Integer limit);
}
