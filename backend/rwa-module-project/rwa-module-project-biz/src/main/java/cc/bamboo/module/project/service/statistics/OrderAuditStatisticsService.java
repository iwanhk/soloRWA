package cc.bamboo.module.project.service.statistics;

import cc.bamboo.module.project.controller.admin.statistics.vo.OrderAuditStatisticsRespVO;

/**
 * 订单审核统计 Service 接口
 *
 * @author Swolf
 */
public interface OrderAuditStatisticsService {

    /**
     * 获取订单审核统计
     *
     * @return 订单审核统计数据
     */
    OrderAuditStatisticsRespVO getOrderAuditStatistics();

}
