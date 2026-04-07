package cc.bamboo.module.chain.controller.admin.systeminitialization;

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

import cc.bamboo.module.chain.controller.admin.systeminitialization.vo.*;
import cc.bamboo.module.chain.dal.dataobject.systeminitialization.SystemInitializationDO;
import cc.bamboo.module.chain.service.systeminitialization.SystemInitializationService;

@Tag(name = "管理后台 - 系统初始化")
@RestController
@RequestMapping("/chain/system-initialization")
@Validated
public class SystemInitializationController {

    @Resource
    private SystemInitializationService systemInitializationService;

    @PostMapping("/create")
    @Operation(summary = "创建系统初始化")
    @PreAuthorize("@ss.hasPermission('chain:system-initialization:create')")
    public CommonResult<Long> createSystemInitialization(@Valid @RequestBody SystemInitializationSaveReqVO createReqVO) {
        return success(systemInitializationService.createSystemInitialization(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新系统初始化")
    @PreAuthorize("@ss.hasPermission('chain:system-initialization:update')")
    public CommonResult<Boolean> updateSystemInitialization(@Valid @RequestBody SystemInitializationSaveReqVO updateReqVO) {
        systemInitializationService.updateSystemInitialization(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除系统初始化")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('chain:system-initialization:delete')")
    public CommonResult<Boolean> deleteSystemInitialization(@RequestParam("id") Long id) {
        systemInitializationService.deleteSystemInitialization(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得系统初始化")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('chain:system-initialization:query')")
    public CommonResult<SystemInitializationRespVO> getSystemInitialization(@RequestParam("id") Long id) {
        SystemInitializationDO systemInitialization = systemInitializationService.getSystemInitialization(id);
        return success(BeanUtils.toBean(systemInitialization, SystemInitializationRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得系统初始化分页")
    @PreAuthorize("@ss.hasPermission('chain:system-initialization:query')")
    public CommonResult<PageResult<SystemInitializationRespVO>> getSystemInitializationPage(@Valid SystemInitializationPageReqVO pageReqVO) {
        PageResult<SystemInitializationDO> pageResult = systemInitializationService.getSystemInitializationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SystemInitializationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出系统初始化 Excel")
    @PreAuthorize("@ss.hasPermission('chain:system-initialization:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSystemInitializationExcel(@Valid SystemInitializationPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SystemInitializationDO> list = systemInitializationService.getSystemInitializationPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "系统初始化.xls", "数据", SystemInitializationRespVO.class,
                        BeanUtils.toBean(list, SystemInitializationRespVO.class));
    }

}