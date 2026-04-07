package cc.bamboo.module.project.controller.admin.statistics;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.project.controller.admin.statistics.vo.ProjectAuditStatisticsRespVO;
import cc.bamboo.module.project.service.statistics.ProjectAuditStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * 项目审核统计 Controller
 *
 * @author Swolf
 */
@Tag(name = "管理后台 - 项目审核统计")
@RestController
@RequestMapping("/project/audit-statistics")
public class ProjectAuditStatisticsController {

    @Resource
    private ProjectAuditStatisticsService projectAuditStatisticsService;

    @GetMapping
    @Operation(summary = "获取项目审核统计")
    @PreAuthorize("@ss.hasPermission('project:info:audit')")
    public CommonResult<ProjectAuditStatisticsRespVO> getProjectAuditStatistics() {
        ProjectAuditStatisticsRespVO result = projectAuditStatisticsService.getProjectAuditStatistics();
        return success(result);
    }

}
