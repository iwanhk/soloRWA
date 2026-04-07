package cc.bamboo.module.project.service.statistics;

import cc.bamboo.module.project.controller.admin.statistics.vo.ProjectAuditStatisticsRespVO;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 项目审核统计 Service 实现类
 *
 * @author Swolf
 */
@Service
@Slf4j
public class ProjectAuditStatisticsServiceImpl implements ProjectAuditStatisticsService {

    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Override
    public ProjectAuditStatisticsRespVO getProjectAuditStatistics() {
        log.info("[getProjectAuditStatistics] 开始获取项目审核统计");

        // 统计待上线审核项目数
        Long projectOnlineAuditCount = projectInfoMapper.countOnlineAudit();

        // 统计待运行审核项目数
        Long projectRunningAuditCount = projectInfoMapper.countRunningAudit();

        ProjectAuditStatisticsRespVO result = ProjectAuditStatisticsRespVO.builder()
                .projectOnlineAuditCount(projectOnlineAuditCount)
                .projectRunningAuditCount(projectRunningAuditCount)
                .build();

        log.info("[getProjectAuditStatistics] 项目审核统计: {}", result);
        return result;
    }

}
