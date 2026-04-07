package cc.bamboo.module.project.controller.admin.projectorderbalancelog;

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

import cc.bamboo.module.project.controller.admin.projectorderbalancelog.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectorderbalancelog.ProjectOrderBalanceLogDO;
import cc.bamboo.module.project.service.projectorderbalancelog.ProjectOrderBalanceLogService;

@Tag(name = "管理后台 - 用户项目余额记录")
@RestController
@RequestMapping("/project/order-balance-log")
@Validated
public class ProjectOrderBalanceLogController {

    @Resource
    private ProjectOrderBalanceLogService orderBalanceLogService;

    @PostMapping("/create")
    @Operation(summary = "创建用户项目余额记录")
    @PreAuthorize("@ss.hasPermission('project:order-balance-log:create')")
    public CommonResult<Long> createOrderBalanceLog(@Valid @RequestBody ProjectOrderBalanceLogSaveReqVO createReqVO) {
        return success(orderBalanceLogService.createOrderBalanceLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户项目余额记录")
    @PreAuthorize("@ss.hasPermission('project:order-balance-log:update')")
    public CommonResult<Boolean> updateOrderBalanceLog(@Valid @RequestBody ProjectOrderBalanceLogSaveReqVO updateReqVO) {
        orderBalanceLogService.updateOrderBalanceLog(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户项目余额记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:order-balance-log:delete')")
    public CommonResult<Boolean> deleteOrderBalanceLog(@RequestParam("id") Long id) {
        orderBalanceLogService.deleteOrderBalanceLog(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户项目余额记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:order-balance-log:query')")
    public CommonResult<ProjectOrderBalanceLogRespVO> getOrderBalanceLog(@RequestParam("id") Long id) {
        ProjectOrderBalanceLogDO orderBalanceLog = orderBalanceLogService.getOrderBalanceLog(id);
        return success(BeanUtils.toBean(orderBalanceLog, ProjectOrderBalanceLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户项目余额记录分页")
    @PreAuthorize("@ss.hasPermission('project:order-balance-log:query')")
    public CommonResult<PageResult<ProjectOrderBalanceLogRespVO>> getOrderBalanceLogPage(@Valid ProjectOrderBalanceLogPageReqVO pageReqVO) {
        PageResult<ProjectOrderBalanceLogDO> pageResult = orderBalanceLogService.getOrderBalanceLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProjectOrderBalanceLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户项目余额记录 Excel")
    @PreAuthorize("@ss.hasPermission('project:order-balance-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOrderBalanceLogExcel(@Valid ProjectOrderBalanceLogPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProjectOrderBalanceLogDO> list = orderBalanceLogService.getOrderBalanceLogPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户项目余额记录.xls", "数据", ProjectOrderBalanceLogRespVO.class,
                        BeanUtils.toBean(list, ProjectOrderBalanceLogRespVO.class));
    }

}