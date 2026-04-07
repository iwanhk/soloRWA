package cc.bamboo.module.project.controller.admin.projectfundconfig;

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

import cc.bamboo.module.project.controller.admin.projectfundconfig.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectfundconfig.ProjectFundConfigDO;
import cc.bamboo.module.project.service.projectfundconfig.ProjectFundConfigService;

@Tag(name = "管理后台 - 基金项目配置")
@RestController
@RequestMapping("/project/fund-config")
@Validated
public class ProjectFundConfigController {

    @Resource
    private ProjectFundConfigService fundConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建基金项目配置")
    @PreAuthorize("@ss.hasPermission('project:fund-config:create')")
    public CommonResult<Long> createFundConfig(@Valid @RequestBody ProjectFundConfigSaveReqVO createReqVO) {
        return success(fundConfigService.createFundConfig(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新基金项目配置")
    @PreAuthorize("@ss.hasPermission('project:fund-config:update')")
    public CommonResult<Boolean> updateFundConfig(@Valid @RequestBody ProjectFundConfigSaveReqVO updateReqVO) {
        fundConfigService.updateFundConfig(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除基金项目配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:fund-config:delete')")
    public CommonResult<Boolean> deleteFundConfig(@RequestParam("id") Long id) {
        fundConfigService.deleteFundConfig(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得基金项目配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:fund-config:query')")
    public CommonResult<ProjectFundConfigRespVO> getFundConfig(@RequestParam("id") Long id) {
        ProjectFundConfigDO fundConfig = fundConfigService.getFundConfig(id);
        return success(BeanUtils.toBean(fundConfig, ProjectFundConfigRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得基金项目配置分页")
    @PreAuthorize("@ss.hasPermission('project:fund-config:query')")
    public CommonResult<PageResult<ProjectFundConfigRespVO>> getFundConfigPage(@Valid ProjectFundConfigPageReqVO pageReqVO) {
        PageResult<ProjectFundConfigDO> pageResult = fundConfigService.getFundConfigPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProjectFundConfigRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出基金项目配置 Excel")
    @PreAuthorize("@ss.hasPermission('project:fund-config:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportFundConfigExcel(@Valid ProjectFundConfigPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProjectFundConfigDO> list = fundConfigService.getFundConfigPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "基金项目配置.xls", "数据", ProjectFundConfigRespVO.class,
                        BeanUtils.toBean(list, ProjectFundConfigRespVO.class));
    }

}