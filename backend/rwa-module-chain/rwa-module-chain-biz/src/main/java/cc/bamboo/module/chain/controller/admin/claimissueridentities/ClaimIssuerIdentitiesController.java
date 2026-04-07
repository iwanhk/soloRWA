package cc.bamboo.module.chain.controller.admin.claimissueridentities;

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

import cc.bamboo.module.chain.controller.admin.claimissueridentities.vo.*;
import cc.bamboo.module.chain.dal.dataobject.claimissueridentities.ClaimIssuerIdentitiesDO;
import cc.bamboo.module.chain.service.claimissueridentities.ClaimIssuerIdentitiesService;

@Tag(name = "管理后台 - 声明发行者身份")
@RestController
@RequestMapping("/chain/claim-issuer-identities")
@Validated
public class ClaimIssuerIdentitiesController {

    @Resource
    private ClaimIssuerIdentitiesService claimIssuerIdentitiesService;

    @PostMapping("/create")
    @Operation(summary = "创建声明发行者身份")
    @PreAuthorize("@ss.hasPermission('chain:claim-issuer-identities:create')")
    public CommonResult<Long> createClaimIssuerIdentities(@Valid @RequestBody ClaimIssuerIdentitiesSaveReqVO createReqVO) {
        return success(claimIssuerIdentitiesService.createClaimIssuerIdentities(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新声明发行者身份")
    @PreAuthorize("@ss.hasPermission('chain:claim-issuer-identities:update')")
    public CommonResult<Boolean> updateClaimIssuerIdentities(@Valid @RequestBody ClaimIssuerIdentitiesSaveReqVO updateReqVO) {
        claimIssuerIdentitiesService.updateClaimIssuerIdentities(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除声明发行者身份")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('chain:claim-issuer-identities:delete')")
    public CommonResult<Boolean> deleteClaimIssuerIdentities(@RequestParam("id") Long id) {
        claimIssuerIdentitiesService.deleteClaimIssuerIdentities(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得声明发行者身份")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('chain:claim-issuer-identities:query')")
    public CommonResult<ClaimIssuerIdentitiesRespVO> getClaimIssuerIdentities(@RequestParam("id") Long id) {
        ClaimIssuerIdentitiesDO claimIssuerIdentities = claimIssuerIdentitiesService.getClaimIssuerIdentities(id);
        return success(BeanUtils.toBean(claimIssuerIdentities, ClaimIssuerIdentitiesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得声明发行者身份分页")
    @PreAuthorize("@ss.hasPermission('chain:claim-issuer-identities:query')")
    public CommonResult<PageResult<ClaimIssuerIdentitiesRespVO>> getClaimIssuerIdentitiesPage(@Valid ClaimIssuerIdentitiesPageReqVO pageReqVO) {
        PageResult<ClaimIssuerIdentitiesDO> pageResult = claimIssuerIdentitiesService.getClaimIssuerIdentitiesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ClaimIssuerIdentitiesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出声明发行者身份 Excel")
    @PreAuthorize("@ss.hasPermission('chain:claim-issuer-identities:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportClaimIssuerIdentitiesExcel(@Valid ClaimIssuerIdentitiesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ClaimIssuerIdentitiesDO> list = claimIssuerIdentitiesService.getClaimIssuerIdentitiesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "声明发行者身份.xls", "数据", ClaimIssuerIdentitiesRespVO.class,
                        BeanUtils.toBean(list, ClaimIssuerIdentitiesRespVO.class));
    }

}