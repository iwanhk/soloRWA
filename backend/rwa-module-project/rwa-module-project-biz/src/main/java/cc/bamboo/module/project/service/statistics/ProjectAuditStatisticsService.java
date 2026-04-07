package cc.bamboo.module.project.service.statistics;

import cc.bamboo.module.project.controller.admin.statistics.vo.ProjectAuditStatisticsRespVO;

/**
 * 项目审核统计 Service 接口
 *
 * @author Swolf
 */
public interface ProjectAuditStatisticsService {

    /**
     * 获取项目审核统计
     *
     * @return 项目审核统计数据
     */
    ProjectAuditStatisticsRespVO getProjectAuditStatistics();

}
