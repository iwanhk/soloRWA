package cc.bamboo.module.user.controller.admin.agreement;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.security.PermitAll;
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

import cc.bamboo.module.user.controller.admin.agreement.vo.*;
import cc.bamboo.module.user.dal.dataobject.agreement.AgreementDO;
import cc.bamboo.module.user.service.agreement.AgreementService;

@Tag(name = "管理后台 - 系统协议表")
@RestController
@RequestMapping("/user/agreement")
@Validated
public class AgreementController {

    @Resource
    private AgreementService agreementService;

    @PostMapping("/create")
    @Operation(summary = "创建系统协议表")
    @PreAuthorize("@ss.hasPermission('user:agreement:create')")
    public CommonResult<Long> createAgreement(@Valid @RequestBody AgreementSaveReqVO createReqVO) {
        return success(agreementService.createAgreement(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新系统协议表")
    @PreAuthorize("@ss.hasPermission('user:agreement:update')")
    public CommonResult<Boolean> updateAgreement(@Valid @RequestBody AgreementSaveReqVO updateReqVO) {
        agreementService.updateAgreement(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除系统协议表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('user:agreement:delete')")
    public CommonResult<Boolean> deleteAgreement(@RequestParam("id") Long id) {
        agreementService.deleteAgreement(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得系统协议表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('user:agreement:query')")
    public CommonResult<AgreementRespVO> getAgreement(@RequestParam("id") Long id) {
        AgreementDO agreement = agreementService.getAgreement(id);
        return success(BeanUtils.toBean(agreement, AgreementRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得系统协议表分页")
    @PreAuthorize("@ss.hasPermission('user:agreement:query')")
    public CommonResult<PageResult<AgreementRespVO>> getAgreementPage(@Valid AgreementPageReqVO pageReqVO) {
        PageResult<AgreementDO> pageResult = agreementService.getAgreementPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AgreementRespVO.class));
    }

    @GetMapping("/simple")
    @Operation(summary = "获得系统协议表简单列表")
    //@PreAuthorize("@ss.hasPermission('user:agreement:query')")
    @PermitAll
    public CommonResult<List<AgreementSimpleRespVO>> getAgreementSimple() {
        List<AgreementDO> list = agreementService.getAgreementSimple();
        return success(BeanUtils.toBean(list, AgreementSimpleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出系统协议表 Excel")
    @PreAuthorize("@ss.hasPermission('user:agreement:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAgreementExcel(@Valid AgreementPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AgreementDO> list = agreementService.getAgreementPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "系统协议表.xls", "数据", AgreementRespVO.class,
                        BeanUtils.toBean(list, AgreementRespVO.class));
    }

}