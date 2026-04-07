package cc.bamboo.module.project.controller.admin.statistics;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.project.controller.admin.statistics.vo.OrderAuditStatisticsRespVO;
import cc.bamboo.module.project.service.statistics.OrderAuditStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * 订单审核统计 Controller
 *
 * @author Swolf
 */
@Tag(name = "管理后台 - 订单审核统计")
@RestController
@RequestMapping("/project/order-audit-statistics")
public class OrderAuditStatisticsController {

    @Resource
    private OrderAuditStatisticsService orderAuditStatisticsService;

    @GetMapping
    @Operation(summary = "获取订单审核统计")
    @PreAuthorize("@ss.hasPermission('project:order:audit')")
    public CommonResult<OrderAuditStatisticsRespVO> getOrderAuditStatistics() {
        OrderAuditStatisticsRespVO result = orderAuditStatisticsService.getOrderAuditStatistics();
        return success(result);
    }

}
