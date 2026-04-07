package cc.bamboo.module.project.service.statistics;

import cc.bamboo.module.project.controller.admin.statistics.vo.BillAuditStatisticsRespVO;

/**
 * 账单审核统计 Service 接口
 *
 * @author Swolf
 */
public interface BillAuditStatisticsService {

    /**
     * 获取账单审核统计
     *
     * @return 账单审核统计数据
     */
    BillAuditStatisticsRespVO getBillAuditStatistics();

}
