package cc.bamboo.module.project.controller.admin.projectorderbalance;

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

import cc.bamboo.module.project.controller.admin.projectorderbalance.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectorderbalance.ProjectOrderBalanceDO;
import cc.bamboo.module.project.service.projectorderbalance.ProjectOrderBalanceService;

@Tag(name = "管理后台 - 用户项目余额表")
@RestController
@RequestMapping("/project/order-balance")
@Validated
public class ProjectOrderBalanceController {

    @Resource
    private ProjectOrderBalanceService orderBalanceService;

    @PostMapping("/create")
    @Operation(summary = "创建用户项目余额表")
    @PreAuthorize("@ss.hasPermission('project:order-balance:create')")
    public CommonResult<Long> createOrderBalance(@Valid @RequestBody ProjectOrderBalanceSaveReqVO createReqVO) {
        return success(orderBalanceService.createOrderBalance(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户项目余额表")
    @PreAuthorize("@ss.hasPermission('project:order-balance:update')")
    public CommonResult<Boolean> updateOrderBalance(@Valid @RequestBody ProjectOrderBalanceSaveReqVO updateReqVO) {
        orderBalanceService.updateOrderBalance(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户项目余额表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:order-balance:delete')")
    public CommonResult<Boolean> deleteOrderBalance(@RequestParam("id") Long id) {
        orderBalanceService.deleteOrderBalance(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户项目余额表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:order-balance:query')")
    public CommonResult<ProjectOrderBalanceRespVO> getOrderBalance(@RequestParam("id") Long id) {
        ProjectOrderBalanceDO orderBalance = orderBalanceService.getOrderBalance(id);
        return success(BeanUtils.toBean(orderBalance, ProjectOrderBalanceRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户项目余额表分页")
    @PreAuthorize("@ss.hasPermission('project:order-balance:query')")
    public CommonResult<PageResult<ProjectOrderBalanceRespVO>> getOrderBalancePage(@Valid ProjectOrderBalancePageReqVO pageReqVO) {
        PageResult<ProjectOrderBalanceDO> pageResult = orderBalanceService.getOrderBalancePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProjectOrderBalanceRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户项目余额表 Excel")
    @PreAuthorize("@ss.hasPermission('project:order-balance:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOrderBalanceExcel(@Valid ProjectOrderBalancePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProjectOrderBalanceDO> list = orderBalanceService.getOrderBalancePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户项目余额表.xls", "数据", ProjectOrderBalanceRespVO.class,
                        BeanUtils.toBean(list, ProjectOrderBalanceRespVO.class));
    }

}