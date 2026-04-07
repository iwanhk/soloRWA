package cc.bamboo.module.project.controller.admin.projectminingconfig;

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

import cc.bamboo.module.project.controller.admin.projectminingconfig.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectminingconfig.ProjectMiningConfigDO;
import cc.bamboo.module.project.service.projectminingconfig.ProjectMiningConfigService;

@Tag(name = "管理后台 - 挖矿项目配置")
@RestController
@RequestMapping("/project/mining-config")
@Validated
public class ProjectMiningConfigController {

    @Resource
    private ProjectMiningConfigService miningConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建挖矿项目配置")
    @PreAuthorize("@ss.hasPermission('project:mining-config:create')")
    public CommonResult<Long> createMiningConfig(@Valid @RequestBody ProjectMiningConfigSaveReqVO createReqVO) {
        return success(miningConfigService.createMiningConfig(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新挖矿项目配置")
    @PreAuthorize("@ss.hasPermission('project:mining-config:update')")
    public CommonResult<Boolean> updateMiningConfig(@Valid @RequestBody ProjectMiningConfigSaveReqVO updateReqVO) {
        miningConfigService.updateMiningConfig(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除挖矿项目配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:mining-config:delete')")
    public CommonResult<Boolean> deleteMiningConfig(@RequestParam("id") Long id) {
        miningConfigService.deleteMiningConfig(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得挖矿项目配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:mining-config:query')")
    public CommonResult<ProjectMiningConfigRespVO> getMiningConfig(@RequestParam("id") Long id) {
        ProjectMiningConfigDO miningConfig = miningConfigService.getMiningConfig(id);
        return success(BeanUtils.toBean(miningConfig, ProjectMiningConfigRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得挖矿项目配置分页")
    @PreAuthorize("@ss.hasPermission('project:mining-config:query')")
    public CommonResult<PageResult<ProjectMiningConfigRespVO>> getMiningConfigPage(@Valid ProjectMiningConfigPageReqVO pageReqVO) {
        PageResult<ProjectMiningConfigDO> pageResult = miningConfigService.getMiningConfigPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProjectMiningConfigRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出挖矿项目配置 Excel")
    @PreAuthorize("@ss.hasPermission('project:mining-config:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMiningConfigExcel(@Valid ProjectMiningConfigPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProjectMiningConfigDO> list = miningConfigService.getMiningConfigPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "挖矿项目配置.xls", "数据", ProjectMiningConfigRespVO.class,
                        BeanUtils.toBean(list, ProjectMiningConfigRespVO.class));
    }

}