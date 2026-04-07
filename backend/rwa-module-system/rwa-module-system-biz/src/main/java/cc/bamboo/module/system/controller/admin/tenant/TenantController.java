package cc.bamboo.module.system.controller.admin.tenant;

import cc.bamboo.framework.apilog.core.annotation.ApiAccessLog;
import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.framework.excel.core.util.ExcelUtils;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantRespVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantSaveReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantSimpleRespVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantCreateWithAuditReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantSubmitAuditReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantAuditReqVO;
import cc.bamboo.module.system.controller.admin.tenant.vo.tenant.TenantAuditStatusRespVO;

import cc.bamboo.module.system.dal.dataobject.tenant.TenantDO;
import cc.bamboo.module.system.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cc.bamboo.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cc.bamboo.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 租户")
@RestController
@RequestMapping("/system/tenant")
public class TenantController {

    @Resource
    private TenantService tenantService;

    @GetMapping("/get-id-by-name")
    @PermitAll
    @Operation(summary = "使用租户名，获得租户编号", description = "登录界面，根据用户的租户名，获得租户编号")
    @Parameter(name = "name", description = "租户名", required = true, example = "1024")
    public CommonResult<Long> getTenantIdByName(@RequestParam("name") String name) {
        TenantDO tenant = tenantService.getTenantByName(name);
        return success(tenant != null ? tenant.getId() : null);
    }

    @GetMapping("/get-by-website")
    @PermitAll
    @Operation(summary = "使用域名，获得租户信息", description = "登录界面，根据用户的域名，获得租户信息")
    @Parameter(name = "website", description = "域名", required = true, example = "www.iocoder.cn")
    public CommonResult<TenantSimpleRespVO> getTenantByWebsite(@RequestParam("website") String website) {
        TenantDO tenant = tenantService.getTenantByWebsite(website);
        return success(BeanUtils.toBean(tenant, TenantSimpleRespVO.class));
    }

    @PostMapping("/create")
    @Operation(summary = "创建租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:create')")
    public CommonResult<Long> createTenant(@Valid @RequestBody TenantSaveReqVO createReqVO) {
        return success(tenantService.createTenant(createReqVO));
    }

    @PostMapping("/create-with-audit")
    @Operation(summary = "创建租户（带审核状态）")
    @PreAuthorize("@ss.hasPermission('system:tenant:create')")
    public CommonResult<Long> createTenantWithAudit(@Valid @RequestBody TenantCreateWithAuditReqVO createReqVO) {
        return success(tenantService.createTenantWithAudit(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:update')")
    public CommonResult<Boolean> updateTenant(@Valid @RequestBody TenantSaveReqVO updateReqVO) {
        tenantService.updateTenant(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除租户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:delete')")
    public CommonResult<Boolean> deleteTenant(@RequestParam("id") Long id) {
        tenantService.deleteTenant(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得租户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<TenantRespVO> getTenant(@RequestParam("id") Long id) {
        TenantDO tenant = tenantService.getTenant(id);
        return success(BeanUtils.toBean(tenant, TenantRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得租户分页")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<PageResult<TenantRespVO>> getTenantPage(@Valid TenantPageReqVO pageVO) {
        PageResult<TenantDO> pageResult = tenantService.getTenantPage(pageVO);
        return success(BeanUtils.toBean(pageResult, TenantRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出租户 Excel")
    @PreAuthorize("@ss.hasPermission('system:tenant:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTenantExcel(@Valid TenantPageReqVO exportReqVO,
            HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TenantDO> list = tenantService.getTenantPage(exportReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "租户.xls", "数据", TenantRespVO.class,
                BeanUtils.toBean(list, TenantRespVO.class));
    }

    // ========== 审核相关接口 ==========

    @PostMapping("/submit-audit")
    @Operation(summary = "提交审核", description = "开发商提交发行商信息进行审核")
    public CommonResult<Boolean> submitAudit(@Valid @RequestBody TenantSubmitAuditReqVO reqVO) {
        tenantService.submitAudit(reqVO);
        return success(true);
    }

    @PostMapping("/audit")
    @Operation(summary = "审核租户", description = "超级管理员审核租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:audit')")
    public CommonResult<Boolean> auditTenant(@Valid @RequestBody TenantAuditReqVO reqVO) {
        tenantService.auditTenant(reqVO);
        return success(true);
    }

    @GetMapping("/auditCount")
    @Operation(summary = "获取当前未审核租户数量")
    @PreAuthorize("@ss.hasPermission('system:tenant:audit')")
    public CommonResult<Long> getAuditCount() {
        return success(tenantService.getAuditCount());
    }

    @GetMapping("/audit-status")
    @Operation(summary = "获取当前租户审核状态")
    public CommonResult<TenantAuditStatusRespVO> getAuditStatus() {
        return success(tenantService.getAuditStatus());
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获取租户精简信息列表")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<List<TenantSimpleRespVO>> getTenantSimpleList() {
        List<TenantDO> list = tenantService.getTenantList();
        return success(BeanUtils.toBean(list, TenantSimpleRespVO.class));
    }

    @GetMapping("/publisher-info")
    @Operation(summary = "获取当前租户的发行商信息", description = "用于提交审核页面的表单回显")
    public CommonResult<TenantSubmitAuditReqVO> getPublisherInfo() {
        return success(tenantService.getPublisherInfo());
    }

    @GetMapping("/publisher-info/{tenantId}")
    @Operation(summary = "获取指定租户的发行商信息", description = "管理员审核时查看发行商信息")
    @Parameter(name = "tenantId", description = "租户编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:audit')")
    public CommonResult<TenantSubmitAuditReqVO> getPublisherInfoByTenantId(@PathVariable("tenantId") Long tenantId) {
        return success(tenantService.getPublisherInfoByTenantId(tenantId));
    }

}
