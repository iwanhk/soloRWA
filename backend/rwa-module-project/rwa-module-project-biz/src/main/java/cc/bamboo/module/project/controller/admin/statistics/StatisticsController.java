package cc.bamboo.module.project.controller.admin.statistics;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.project.controller.admin.statistics.vo.*;
import cc.bamboo.module.project.service.statistics.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 首页统计
 */
@Tag(name = "管理后台 - 首页统计")
@RestController
@RequestMapping("/project/statistics")
@Validated
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @GetMapping("/overview")
    @Operation(summary = "获取概览统计数据", description = "项目总数、订单总量、交易总额")
    public CommonResult<StatsOverviewRespVO> getOverview() {
        return success(statisticsService.getOverview());
    }

    @GetMapping("/order-trend")
    @Operation(summary = "获取订单趋势", description = "近30天订单趋势数据")
    public CommonResult<List<OrderTrendRespVO>> getOrderTrend() {
        return success(statisticsService.getOrderTrend());
    }

    @GetMapping("/order-status-dist")
    @Operation(summary = "获取订单状态分布")
    public CommonResult<List<OrderStatusDistRespVO>> getOrderStatusDist() {
        return success(statisticsService.getOrderStatusDist());
    }

    @GetMapping("/top-projects")
    @Operation(summary = "获取热门项目排行")
    @Parameter(name = "limit", description = "返回数量", example = "5")
    public CommonResult<List<TopProjectRespVO>> getTopProjects(
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit) {
        return success(statisticsService.getTopProjects(limit));
    }

    @GetMapping("/recent-orders")
    @Operation(summary = "获取最新订单")
    @Parameter(name = "limit", description = "返回数量", example = "10")
    public CommonResult<List<RecentOrderRespVO>> getRecentOrders(
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        return success(statisticsService.getRecentOrders(limit));
    }
}
