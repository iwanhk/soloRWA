package cc.bamboo.module.project.controller.admin.statistics;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.project.controller.admin.statistics.vo.BillAuditStatisticsRespVO;
import cc.bamboo.module.project.service.statistics.BillAuditStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * 账单审核统计 Controller
 *
 * @author Swolf
 */
@Tag(name = "管理后台 - 账单审核统计")
@RestController
@RequestMapping("/project/bill-audit-statistics")
public class BillAuditStatisticsController {

    @Resource
    private BillAuditStatisticsService billAuditStatisticsService;

    @GetMapping
    @Operation(summary = "获取账单审核统计")
    @PreAuthorize("@ss.hasPermission('project:bill:audit')")
    public CommonResult<BillAuditStatisticsRespVO> getBillAuditStatistics() {
        BillAuditStatisticsRespVO result = billAuditStatisticsService.getBillAuditStatistics();
        return success(result);
    }

}
