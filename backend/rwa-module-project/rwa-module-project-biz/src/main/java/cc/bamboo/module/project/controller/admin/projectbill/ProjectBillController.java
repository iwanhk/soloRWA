package cc.bamboo.module.project.controller.admin.projectbill;

import cc.bamboo.framework.security.core.util.SecurityFrameworkUtils;
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

import cc.bamboo.module.project.controller.admin.projectbill.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectbill.ProjectBillDO;
import cc.bamboo.module.project.service.projectbill.ProjectBillService;

@Tag(name = "管理后台 - 项目账单管理表")
@RestController
@RequestMapping("/project/bill")
@Validated
public class ProjectBillController {

    @Resource
    private ProjectBillService billService;

    @PostMapping("/create")
    @Operation(summary = "创建项目账单管理表")
    @PreAuthorize("@ss.hasPermission('project:bill:create')")
    public CommonResult<Long> createBill(@Valid @RequestBody ProjectBillSaveReqVO createReqVO) {
        return success(billService.createBill(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新项目账单管理表")
    @PreAuthorize("@ss.hasPermission('project:bill:update')")
    public CommonResult<Boolean> updateBill(@Valid @RequestBody ProjectBillSaveReqVO updateReqVO) {
        billService.updateBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除项目账单管理表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:bill:delete')")
    public CommonResult<Boolean> deleteBill(@RequestParam("id") Long id) {
        billService.deleteBill(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得项目账单详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:bill:query')")
    public CommonResult<ProjectBillRespVO> getBill(@RequestParam("id") Long id) {
        ProjectBillRespVO bill = billService.getBillDetail(id);
        return success(bill);
    }

    @GetMapping("/page")
    @Operation(summary = "获得项目账单管理表分页")
    @PreAuthorize("@ss.hasPermission('project:bill:query')")
    public CommonResult<PageResult<ProjectBillRespVO>> getBillPage(@Valid ProjectBillPageReqVO pageReqVO) {
        PageResult<ProjectBillRespVO> pageResult = billService.getBillPageWithUserName(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出项目账单管理表 Excel")
    @PreAuthorize("@ss.hasPermission('project:bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportBillExcel(@Valid ProjectBillPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<ProjectBillRespVO> pageResult = billService.getBillPageWithUserName(pageReqVO);
        // 导出 Excel
        ExcelUtils.write(response, "项目账单管理表.xls", "数据", ProjectBillRespVO.class,
                        pageResult.getList());
    }

    @PutMapping("/audit")
    @Operation(summary = "审核账单")
    @PreAuthorize("@ss.hasPermission('project:bill:audit')")
    @ApiAccessLog(operateType = UPDATE)
    public CommonResult<Boolean> auditBill(@Valid @RequestBody ProjectBillAuditReqVO auditReqVO) {
        // 获取当前登录用户信息
        Long auditUserId = SecurityFrameworkUtils.getLoginUserId();
        String auditUserName = SecurityFrameworkUtils.getLoginUserNickname();
        
        billService.auditBill(auditReqVO, auditUserId, auditUserName);
        return success(true);
    }

    @PutMapping("/upload-voucher")
    @Operation(summary = "上传支付凭证")
    @PreAuthorize("@ss.hasPermission('project:bill:upload-voucher')")
    @ApiAccessLog(operateType = UPDATE)
    public CommonResult<Boolean> uploadPayVoucher(@Valid @RequestBody ProjectBillUploadVoucherReqVO uploadReqVO) {
        billService.uploadPayVoucher(uploadReqVO);
        return success(true);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获得账单统计")
    @PreAuthorize("@ss.hasPermission('project:bill:query')")
    public CommonResult<ProjectBillStatisticsRespVO> getBillStatistics(@Valid ProjectBillStatisticsReqVO reqVO) {
        ProjectBillStatisticsRespVO statistics = billService.getBillStatistics(reqVO);
        return success(statistics);
    }

}