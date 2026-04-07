package cc.bamboo.module.project.controller.admin.projectdividendperiod;

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

import cc.bamboo.module.project.controller.admin.projectdividendperiod.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectdividendperiod.ProjectDividendPeriodDO;
import cc.bamboo.module.project.service.projectdividendperiod.ProjectDividendPeriodService;

@Tag(name = "管理后台 - 分红周期")
@RestController
@RequestMapping("/project/dividend-period")
@Validated
public class ProjectDividendPeriodController {

    @Resource
    private ProjectDividendPeriodService dividendPeriodService;

    @PostMapping("/create")
    @Operation(summary = "创建分红周期")
    @PreAuthorize("@ss.hasPermission('project:dividend-period:create')")
    public CommonResult<Long> createDividendPeriod(@Valid @RequestBody ProjectDividendPeriodSaveReqVO createReqVO) {
        return success(dividendPeriodService.createDividendPeriod(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新分红周期")
    @PreAuthorize("@ss.hasPermission('project:dividend-period:update')")
    public CommonResult<Boolean> updateDividendPeriod(@Valid @RequestBody ProjectDividendPeriodSaveReqVO updateReqVO) {
        dividendPeriodService.updateDividendPeriod(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除分红周期")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:dividend-period:delete')")
    public CommonResult<Boolean> deleteDividendPeriod(@RequestParam("id") Long id) {
        dividendPeriodService.deleteDividendPeriod(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得分红周期")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:dividend-period:query')")
    public CommonResult<ProjectDividendPeriodRespVO> getDividendPeriod(@RequestParam("id") Long id) {
        ProjectDividendPeriodDO dividendPeriod = dividendPeriodService.getDividendPeriod(id);
        return success(BeanUtils.toBean(dividendPeriod, ProjectDividendPeriodRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得分红周期分页")
    @PreAuthorize("@ss.hasPermission('project:dividend-period:query')")
    public CommonResult<PageResult<ProjectDividendPeriodRespVO>> getDividendPeriodPage(@Valid ProjectDividendPeriodPageReqVO pageReqVO) {
        PageResult<ProjectDividendPeriodDO> pageResult = dividendPeriodService.getDividendPeriodPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProjectDividendPeriodRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出分红周期 Excel")
    @PreAuthorize("@ss.hasPermission('project:dividend-period:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDividendPeriodExcel(@Valid ProjectDividendPeriodPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProjectDividendPeriodDO> list = dividendPeriodService.getDividendPeriodPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "分红周期.xls", "数据", ProjectDividendPeriodRespVO.class,
                        BeanUtils.toBean(list, ProjectDividendPeriodRespVO.class));
    }

}