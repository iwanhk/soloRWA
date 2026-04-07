package cc.bamboo.module.project.controller.admin.projectoperation;

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

import cc.bamboo.module.project.controller.admin.projectoperation.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectoperation.ProjectOperationDO;
import cc.bamboo.module.project.service.projectoperation.ProjectOperationService;

@Tag(name = "管理后台 - 项目运营统计表")
@RestController
@RequestMapping("/project/operation")
@Validated
public class ProjectOperationController {

    @Resource
    private ProjectOperationService operationService;

    @PostMapping("/create")
    @Operation(summary = "创建项目运营统计表")
    @PreAuthorize("@ss.hasPermission('project:operation:create')")
    public CommonResult<Long> createOperation(@Valid @RequestBody ProjectOperationSaveReqVO createReqVO) {
        return success(operationService.createOperation(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新项目运营统计表")
    @PreAuthorize("@ss.hasPermission('project:operation:update')")
    public CommonResult<Boolean> updateOperation(@Valid @RequestBody ProjectOperationSaveReqVO updateReqVO) {
        operationService.updateOperation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除项目运营统计表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:operation:delete')")
    public CommonResult<Boolean> deleteOperation(@RequestParam("id") Long id) {
        operationService.deleteOperation(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得项目运营统计表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:operation:query')")
    public CommonResult<ProjectOperationRespVO> getOperation(@RequestParam("id") Long id) {
        ProjectOperationDO operation = operationService.getOperation(id);
        return success(BeanUtils.toBean(operation, ProjectOperationRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得项目运营统计表分页")
    @PreAuthorize("@ss.hasPermission('project:operation:query')")
    public CommonResult<PageResult<ProjectOperationRespVO>> getOperationPage(@Valid ProjectOperationPageReqVO pageReqVO) {
        PageResult<ProjectOperationDO> pageResult = operationService.getOperationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProjectOperationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出项目运营统计表 Excel")
    @PreAuthorize("@ss.hasPermission('project:operation:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOperationExcel(@Valid ProjectOperationPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProjectOperationDO> list = operationService.getOperationPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "项目运营统计表.xls", "数据", ProjectOperationRespVO.class,
                        BeanUtils.toBean(list, ProjectOperationRespVO.class));
    }

}