package cc.bamboo.module.user.controller.admin.statistics;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.user.controller.admin.statistics.vo.UserAuditStatisticsRespVO;
import cc.bamboo.module.user.service.statistics.UserAuditStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * 用户审核统计 Controller
 *
 * @author Swolf
 */
@Tag(name = "管理后台 - 用户审核统计")
@RestController
@RequestMapping("/user/audit-statistics")
public class UserAuditStatisticsController {

    @Resource
    private UserAuditStatisticsService userAuditStatisticsService;

    @GetMapping
    @Operation(summary = "获取用户审核统计")
    @PreAuthorize("@ss.hasPermission('user:audit:review')")
    public CommonResult<UserAuditStatisticsRespVO> getUserAuditStatistics() {
        UserAuditStatisticsRespVO result = userAuditStatisticsService.getUserAuditStatistics();
        return success(result);
    }

}
