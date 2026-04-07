package cc.bamboo.module.user.service.statistics;

import cc.bamboo.module.user.controller.admin.statistics.vo.UserAuditStatisticsRespVO;

/**
 * 用户审核统计 Service 接口
 *
 * @author Swolf
 */
public interface UserAuditStatisticsService {

    /**
     * 获取用户审核统计
     *
     * @return 用户审核统计数据
     */
    UserAuditStatisticsRespVO getUserAuditStatistics();

}
