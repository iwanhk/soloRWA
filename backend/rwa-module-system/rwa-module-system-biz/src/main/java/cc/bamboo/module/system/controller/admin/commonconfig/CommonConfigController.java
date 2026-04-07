package cc.bamboo.module.system.controller.admin.commonconfig;

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

import cc.bamboo.module.system.controller.admin.commonconfig.vo.*;
import cc.bamboo.module.system.dal.dataobject.commonconfig.CommonConfigDO;
import cc.bamboo.module.system.service.commonconfig.CommonConfigService;

@Tag(name = "管理后台 - 参数配置")
@RestController
@RequestMapping("/system/common-config")
@Validated
public class CommonConfigController {

    @Resource
    private CommonConfigService commonConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建参数配置")
    @PreAuthorize("@ss.hasPermission('system:common-config:create')")
    public CommonResult<Long> createCommonConfig(@Valid @RequestBody CommonConfigSaveReqVO createReqVO) {
        return success(commonConfigService.createCommonConfig(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新参数配置")
    @PreAuthorize("@ss.hasPermission('system:common-config:update')")
    public CommonResult<Boolean> updateCommonConfig(@Valid @RequestBody CommonConfigSaveReqVO updateReqVO) {
        commonConfigService.updateCommonConfig(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除参数配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:common-config:delete')")
    public CommonResult<Boolean> deleteCommonConfig(@RequestParam("id") Long id) {
        commonConfigService.deleteCommonConfig(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得参数配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:common-config:query')")
    public CommonResult<CommonConfigRespVO> getCommonConfig(@RequestParam("id") Long id) {
        CommonConfigDO commonConfig = commonConfigService.getCommonConfig(id);
        return success(BeanUtils.toBean(commonConfig, CommonConfigRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得参数配置分页")
    @PreAuthorize("@ss.hasPermission('system:common-config:query')")
    public CommonResult<PageResult<CommonConfigRespVO>> getCommonConfigPage(@Valid CommonConfigPageReqVO pageReqVO) {
        PageResult<CommonConfigDO> pageResult = commonConfigService.getCommonConfigPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CommonConfigRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出参数配置 Excel")
    @PreAuthorize("@ss.hasPermission('system:common-config:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCommonConfigExcel(@Valid CommonConfigPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CommonConfigDO> list = commonConfigService.getCommonConfigPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "参数配置.xls", "数据", CommonConfigRespVO.class,
                        BeanUtils.toBean(list, CommonConfigRespVO.class));
    }

}