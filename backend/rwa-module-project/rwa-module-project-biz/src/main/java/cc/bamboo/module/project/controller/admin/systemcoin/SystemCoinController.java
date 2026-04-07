package cc.bamboo.module.project.controller.admin.systemcoin;

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

import cc.bamboo.module.project.controller.admin.systemcoin.vo.*;
import cc.bamboo.module.project.dal.dataobject.systemcoin.SystemCoinDO;
import cc.bamboo.module.project.service.systemcoin.SystemCoinService;

@Tag(name = "管理后台 - 币种管理")
@RestController
@RequestMapping("/project/system-coin")
@Validated
public class SystemCoinController {

    @Resource
    private SystemCoinService systemCoinService;

    @PostMapping("/create")
    @Operation(summary = "创建币种管理")
    @PreAuthorize("@ss.hasPermission('project:system-coin:create')")
    public CommonResult<Long> createSystemCoin(@Valid @RequestBody SystemCoinSaveReqVO createReqVO) {
        return success(systemCoinService.createSystemCoin(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新币种管理")
    @PreAuthorize("@ss.hasPermission('project:system-coin:update')")
    public CommonResult<Boolean> updateSystemCoin(@Valid @RequestBody SystemCoinSaveReqVO updateReqVO) {
        systemCoinService.updateSystemCoin(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除币种管理")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:system-coin:delete')")
    public CommonResult<Boolean> deleteSystemCoin(@RequestParam("id") Long id) {
        systemCoinService.deleteSystemCoin(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得币种管理")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:system-coin:query')")
    public CommonResult<SystemCoinRespVO> getSystemCoin(@RequestParam("id") Long id) {
        SystemCoinDO systemCoin = systemCoinService.getSystemCoin(id);
        return success(BeanUtils.toBean(systemCoin, SystemCoinRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得币种管理分页")
    @PreAuthorize("@ss.hasPermission('project:system-coin:query')")
    public CommonResult<PageResult<SystemCoinRespVO>> getSystemCoinPage(@Valid SystemCoinPageReqVO pageReqVO) {
        PageResult<SystemCoinDO> pageResult = systemCoinService.getSystemCoinPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SystemCoinRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出币种管理 Excel")
    @PreAuthorize("@ss.hasPermission('project:system-coin:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSystemCoinExcel(@Valid SystemCoinPageReqVO pageReqVO,
            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SystemCoinDO> list = systemCoinService.getSystemCoinPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "币种管理.xls", "数据", SystemCoinRespVO.class,
                BeanUtils.toBean(list, SystemCoinRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得币种下拉列表")
    @Parameter(name = "coinType", description = "币种类型:1-数字货币 2-法币", example = "1")
    public CommonResult<List<SystemCoinSimpleRespVO>> getSystemCoinList(
            @RequestParam(value = "coinType", required = false) Integer coinType) {
        List<SystemCoinDO> list = systemCoinService.getSystemCoinListByType(coinType);
        return success(BeanUtils.toBean(list, SystemCoinSimpleRespVO.class));
    }

}