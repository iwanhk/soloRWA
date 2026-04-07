package cc.bamboo.module.project.controller.admin.projectrevenue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.security.PermitAll;
import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import static cc.bamboo.framework.common.pojo.CommonResult.success;

import cc.bamboo.framework.excel.core.util.ExcelUtils;

import cc.bamboo.framework.apilog.core.annotation.ApiAccessLog;
import static cc.bamboo.framework.apilog.core.enums.OperateTypeEnum.*;

import cc.bamboo.module.project.controller.admin.projectrevenue.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectrevenue.ProjectRevenueDO;
import cc.bamboo.module.project.service.projectrevenue.ProjectRevenueService;

@Tag(name = "管理后台 - 项目收益")
@RestController
@RequestMapping("/project/revenue")
@Validated
@Slf4j
public class ProjectRevenueController {

    @Resource
    private ProjectRevenueService revenueService;

    @PostMapping("/create")
    @Operation(summary = "创建项目收益")
    @PreAuthorize("@ss.hasPermission('project:revenue:create')")
    public CommonResult<Long> createRevenue(@Valid @RequestBody ProjectRevenueSaveReqVO createReqVO) {
        return success(revenueService.createRevenue(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新项目收益")
    @PreAuthorize("@ss.hasPermission('project:revenue:update')")
    public CommonResult<Boolean> updateRevenue(@Valid @RequestBody ProjectRevenueSaveReqVO updateReqVO) {
        revenueService.updateRevenue(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除项目收益")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:revenue:delete')")
    public CommonResult<Boolean> deleteRevenue(@RequestParam("id") Long id) {
        revenueService.deleteRevenue(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得项目收益")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:revenue:query')")
    public CommonResult<ProjectRevenueRespVO> getRevenue(@RequestParam("id") Long id) {
        ProjectRevenueDO revenue = revenueService.getRevenue(id);
        return success(BeanUtils.toBean(revenue, ProjectRevenueRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得项目收益分页")
    @PreAuthorize("@ss.hasPermission('project:revenue:query')")
    public CommonResult<PageResult<ProjectRevenueRespVO>> getRevenuePage(@Valid ProjectRevenuePageReqVO pageReqVO) {
        PageResult<ProjectRevenueDO> pageResult = revenueService.getRevenuePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProjectRevenueRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出项目收益 Excel")
    @PreAuthorize("@ss.hasPermission('project:revenue:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportRevenueExcel(@Valid ProjectRevenuePageReqVO pageReqVO,
            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProjectRevenueDO> list = revenueService.getRevenuePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "项目收益.xls", "数据", ProjectRevenueRespVO.class,
                BeanUtils.toBean(list, ProjectRevenueRespVO.class));
    }

    @PutMapping("/update-available-revenue")
    @Operation(summary = "修改可发放收益", description = "只能修改未发放状态的收益,会重新计算订单收益")
    @PreAuthorize("@ss.hasPermission('project:revenue:update')")
    public CommonResult<Boolean> updateAvailableRevenue(@Valid @RequestBody UpdateAvailableRevenueReqVO reqVO) {
        revenueService.updateAvailableRevenue(reqVO);
        return success(true);
    }

    @GetMapping("/calculate-costs")
    @Operation(summary = "自动计算成本", description = "根据项目配置和收益自动计算电力成本、人力成本和可发放收益")
    @PreAuthorize("@ss.hasPermission('project:revenue:query')")
    public CommonResult<CalculateCostsRespVO> calculateCosts(
            @RequestParam("projectId") Long projectId,
            @RequestParam("revenue") java.math.BigDecimal revenue,
            @RequestParam("revenueDate") @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate revenueDate) {
        return success(revenueService.calculateCosts(projectId, revenue, revenueDate));
    }

    @GetMapping("/calculate-pool-revenue")
    @Operation(summary = "获取矿池收益", description = "获取指定日期的矿池收益数据和汇率")
    @PreAuthorize("@ss.hasPermission('project:revenue:query')")
    public CommonResult<PoolRevenueCalcRespVO> calculatePoolRevenue(
            @RequestParam("projectId") Long projectId,
            @RequestParam("revenueDate") @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate revenueDate) {
        return success(revenueService.calculatePoolRevenue(projectId, revenueDate));
    }

    @GetMapping("/get-coin-usd-rate")
    @Operation(summary = "获取币种兑换USD的汇率", description = "获取指定币种(如BTC)兑USD的实时汇率")
    @PreAuthorize("@ss.hasPermission('project:revenue:query')")
    public CommonResult<java.math.BigDecimal> getCoinUsdRate(
            @RequestParam(value = "coinCode", required = false, defaultValue = "BTC") String coinCode) {
        return success(revenueService.getCoinUsdRate(coinCode));
    }

    // ==================== 测试接口 ====================

    @PostMapping("/test/generate-manual")
    @Operation(summary = "测试-手动生成单日收益", description = "用于测试手动生成模拟数据")
    @PermitAll
    public CommonResult<Long> generateManualRevenue(
            @RequestParam("projectId") Long projectId,
            @RequestParam("revenueDate") @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate revenueDate,
            @RequestParam("revenue") java.math.BigDecimal revenue) {
        return success(revenueService.generateDailyRevenueMork(projectId, revenueDate, revenue));
    }

    @PostMapping("/test/generate-revenue")
    @Operation(summary = "测试-批量生成项目收益", description = "按日期范围循环生成收益，金额随机波动10%")
    @PermitAll
    public CommonResult<List<Long>> testGenerateRevenue(
            @RequestParam("projectId") Long projectId,
            @RequestParam("startDate") @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate startDate,
            @RequestParam("endDate") @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate endDate,
            @RequestParam("baseAmount") java.math.BigDecimal baseAmount) {

        List<Long> revenueIds = new java.util.ArrayList<>();
        java.time.LocalDate currentDate = startDate;
        java.util.Random random = new java.util.Random();

        while (!currentDate.isAfter(endDate)) {
            // 金额随机波动 ±10%
            double fluctuation = 0.9 + (random.nextDouble() * 0.2); // 0.9 ~ 1.1
            java.math.BigDecimal dailyAmount = baseAmount.multiply(java.math.BigDecimal.valueOf(fluctuation))
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            Long revenueId = revenueService.generateDailyRevenueMork(projectId, currentDate, dailyAmount);
            if (revenueId != null) {
                revenueIds.add(revenueId);
            }
            currentDate = currentDate.plusDays(1);
        }

        return success(revenueIds);
    }

    @PostMapping("/test/calculate-order-income")
    @Operation(summary = "测试-批量计算订单收益", description = "按收益ID范围循环计算用户订单收益")
    @PermitAll
    public CommonResult<Integer> testCalculateOrderIncome(
            @RequestParam("startId") Long startId,
            @RequestParam("endId") Long endId) {

        int count = 0;
        for (Long id = startId; id <= endId; id++) {
            try {
                revenueService.calculateOrderIncome(id);
                count++;
            } catch (Exception e) {
                // 忽略错误，继续处理下一个
                log.error("出错了:" + e.getMessage());
            }
        }

        return success(count);
    }

    @PostMapping("/test/distribute-income")
    @Operation(summary = "测试-批量发放收益", description = "按收益ID范围循环发放用户收益")
    @PermitAll
    public CommonResult<Integer> testDistributeIncome(
            @RequestParam("startId") Long startId,
            @RequestParam("endId") Long endId) {

        int count = 0;
        for (Long id = startId; id <= endId; id++) {
            try {
                revenueService.distributeIncome(id);
                count++;
            } catch (Exception e) {
                // 忽略错误，继续处理下一个
            }
        }

        return success(count);
    }

}