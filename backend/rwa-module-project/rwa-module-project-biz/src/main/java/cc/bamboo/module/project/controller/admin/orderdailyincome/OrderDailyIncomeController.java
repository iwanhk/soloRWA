package cc.bamboo.module.project.controller.admin.orderdailyincome;

import cc.bamboo.module.project.service.projectrevenue.ProjectRevenueService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

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

import cc.bamboo.module.project.controller.admin.orderdailyincome.vo.*;
import cc.bamboo.module.project.dal.dataobject.orderdailyincome.OrderDailyIncomeDO;
import cc.bamboo.module.project.service.orderdailyincome.OrderDailyIncomeService;

@Tag(name = "管理后台 - 订单每日收益统计")
@RestController
@RequestMapping("/project/order-daily-income")
@Validated
public class OrderDailyIncomeController {

    @Resource
    private OrderDailyIncomeService orderDailyIncomeService;

    @Resource
    private ProjectRevenueService projectRevenueService;

    @PostMapping("/create")
    @Operation(summary = "创建订单每日收益统计")
    @PreAuthorize("@ss.hasPermission('project:order-daily-income:create')")
    public CommonResult<Long> createOrderDailyIncome(@Valid @RequestBody OrderDailyIncomeSaveReqVO createReqVO) {
        return success(orderDailyIncomeService.createOrderDailyIncome(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新订单每日收益统计")
    @PreAuthorize("@ss.hasPermission('project:order-daily-income:update')")
    public CommonResult<Boolean> updateOrderDailyIncome(@Valid @RequestBody OrderDailyIncomeSaveReqVO updateReqVO) {
        orderDailyIncomeService.updateOrderDailyIncome(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除订单每日收益统计")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:order-daily-income:delete')")
    public CommonResult<Boolean> deleteOrderDailyIncome(@RequestParam("id") Long id) {
        orderDailyIncomeService.deleteOrderDailyIncome(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得订单每日收益统计")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:order-daily-income:query')")
    public CommonResult<OrderDailyIncomeRespVO> getOrderDailyIncome(@RequestParam("id") Long id) {
        OrderDailyIncomeDO orderDailyIncome = orderDailyIncomeService.getOrderDailyIncome(id);
        return success(BeanUtils.toBean(orderDailyIncome, OrderDailyIncomeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得订单每日收益统计分页")
    @PreAuthorize("@ss.hasPermission('project:order-daily-income:query')")
    public CommonResult<PageResult<OrderDailyIncomeRespVO>> getOrderDailyIncomePage(@Valid OrderDailyIncomePageReqVO pageReqVO) {
        PageResult<OrderDailyIncomeDO> pageResult = orderDailyIncomeService.getOrderDailyIncomePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, OrderDailyIncomeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出订单每日收益统计 Excel")
    @PreAuthorize("@ss.hasPermission('project:order-daily-income:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOrderDailyIncomeExcel(@Valid OrderDailyIncomePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OrderDailyIncomeDO> list = orderDailyIncomeService.getOrderDailyIncomePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "订单每日收益统计.xls", "数据", OrderDailyIncomeRespVO.class,
                        BeanUtils.toBean(list, OrderDailyIncomeRespVO.class));
    }

    // 发放收益
    @PostMapping("/distribute-income")
    @Operation(summary = "发放收益")
    @PreAuthorize("@ss.hasPermission('project:order-daily-income:distribute-income')")
    public CommonResult<Boolean> distributeIncome(@Valid @RequestBody OrderDailyIncomeDistributeReqVO distributeReqVO) {
        projectRevenueService.distributeIncome(distributeReqVO.getIncomeDate());
        return success(true);
    }

}