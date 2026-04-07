package cc.bamboo.module.project.controller.admin.projectinfo;

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

import cc.bamboo.module.project.controller.admin.projectinfo.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.service.projectinfo.ProjectInfoExportService;
import cc.bamboo.module.project.service.projectinfo.ProjectInfoService;

@Tag(name = "管理后台 - 项目核心表（基础+状态）")
@RestController
@RequestMapping("/project/info")
@Validated
public class ProjectInfoController {

    @Resource
    private ProjectInfoService infoService;

    @Resource
    private ProjectInfoExportService projectInfoExportService;

    @PostMapping("/create")
    @Operation(summary = "创建项目核心表（基础+状态）")
    @PreAuthorize("@ss.hasPermission('project:info:create')")
    public CommonResult<Long> createInfo(@Valid @RequestBody ProjectInfoSaveReqVO createReqVO) {
        return success(infoService.createInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新项目核心表（基础+状态）")
    @PreAuthorize("@ss.hasPermission('project:info:update')")
    public CommonResult<Boolean> updateInfo(@Valid @RequestBody ProjectInfoUpdateReqVO updateReqVO) {
        infoService.updateInfo(updateReqVO);
        return success(true);
    }

    @PutMapping("/submitAudit")
    @Operation(summary = "提交项目审核")
    @PreAuthorize("@ss.hasPermission('project:info:update')")
    public CommonResult<Boolean> submitAudit(@Valid @RequestBody SubmitAuditReqVO submitReqVO) {
        infoService.submitAudit(submitReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除项目核心表（基础+状态）")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:info:delete')")
    public CommonResult<Boolean> deleteInfo(@RequestParam("id") Long id) {
        infoService.deleteInfo(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得项目核心表（基础+状态）")
    @Parameter(name = "projectId", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:info:query')")
    public CommonResult<ProjectInfoRespVO> getInfo(@RequestParam("projectId") Long projectId) {
        ProjectInfoDO info = infoService.getInfo(projectId);
        ProjectInfoRespVO result = BeanUtils.toBean(info, ProjectInfoRespVO.class);
        // 设置是否有私钥
        // result.setHasPoolPrivateKey(cn.hutool.core.util.StrUtil.isNotBlank(info.getPoolPrivateKey()));
        return success(result);
    }

    @GetMapping("/page")
    @Operation(summary = "获得项目核心表（基础+状态）分页")
    @PreAuthorize("@ss.hasPermission('project:info:query')")
    public CommonResult<PageResult<ProjectInfoListRespVO>> getInfoPage(@Valid ProjectInfoPageReqVO pageReqVO) {
        PageResult<ProjectInfoDO> pageResult = infoService.getInfoPage(pageReqVO);
        PageResult<ProjectInfoListRespVO> result = BeanUtils.toBean(pageResult, ProjectInfoListRespVO.class);
        // 设置是否有私钥
        /*
         * result.getList().forEach(item -> {
         * // 这里需要再次查询DO或者在DO转换时处理，因为VO没有私钥字段。
         * // 简单的做法是遍历DO列表来设置VO的属性，或者修改BeanUtils转换逻辑
         * // 这里由于已经转成了VO，且VO里没有poolPrivateKey字段，我们无法直接判断。
         * // 更好的方式是手动转换列表或使用Map辅助
         * ProjectInfoDO info = pageResult.getList().stream()
         * .filter(i -> i.getProjectId().equals(item.getProjectId()))
         * .findFirst().orElse(null);
         * if (info != null) {
         * // item.setHasPoolPrivateKey(cn.hutool.core.util.StrUtil.isNotBlank(info.
         * getPoolPrivateKey()));
         * }
         * });
         */
        return success(result);
    }

    @GetMapping("/pageSimple")
    @Operation(summary = "获得项目简易下拉")
    @PreAuthorize("@ss.hasPermission('project:info:query')")
    public CommonResult<List<ProjectInfoSimpleRespVO>> getInfoSimple() {
        List<ProjectInfoDO> result = infoService.getInfoList();
        return success(BeanUtils.toBean(result, ProjectInfoSimpleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出项目核心表（基础+状态） Excel")
    @PreAuthorize("@ss.hasPermission('project:info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportInfoExcel(@Valid ProjectInfoPageReqVO pageReqVO,
            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProjectInfoDO> list = infoService.getInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "项目核心表（基础+状态）.xls", "数据", ProjectInfoRespVO.class,
                BeanUtils.toBean(list, ProjectInfoRespVO.class));
    }

    @GetMapping("/export-summary-excel")
    @Operation(summary = "导出项目汇总（按项目分sheet） Excel")
    @PreAuthorize("@ss.hasPermission('project:info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportProjectSummaryExcel(@RequestParam(value = "projectIds", required = false) String projectIds,
            HttpServletResponse response) throws IOException {
        List<Long> ids = new ArrayList<>();
        if (projectIds != null && !projectIds.trim().isEmpty()) {
            String[] parts = projectIds.split(",");
            for (String part : parts) {
                if (part == null) {
                    continue;
                }
                String trimmed = part.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                ids.add(Long.valueOf(trimmed));
            }
        }
        projectInfoExportService.exportProjectSummaryExcel(ids, response);
    }

    @PutMapping("/audit")
    @Operation(summary = "审核项目")
    @PreAuthorize("@ss.hasPermission('project:info:audit')")
    public CommonResult<Boolean> auditProject(@Valid @RequestBody ProjectInfoAuditReqVO auditReqVO) {
        // 获取当前登录用户信息
        Long auditUserId = SecurityFrameworkUtils.getLoginUserId();
        String auditUserName = SecurityFrameworkUtils.getLoginUserNickname();

        infoService.auditProject(auditReqVO, auditUserId, auditUserName);
        if (auditReqVO.getApproved()) {
            infoService.sendProjectAuditPassMessage(auditReqVO.getProjectId());
        }
        return success(true);
    }

    @PutMapping("/sell-status")
    @Operation(summary = "修改项目上下架状态")
    @PreAuthorize("@ss.hasPermission('project:info:sell-status')")
    public CommonResult<Boolean> updateSellStatus(@Valid @RequestBody ProjectInfoSellStatusReqVO sellStatusReqVO) {
        infoService.updateSellStatus(sellStatusReqVO);
        return success(true);
    }

    @PutMapping("/config")
    @Operation(summary = "更新项目配置", description = "配置算力、电费、成本等信息")
    @PreAuthorize("@ss.hasPermission('project:info:update')")
    public CommonResult<Boolean> updateConfig(@Valid @RequestBody ProjectConfigReqVO configReqVO) {
        infoService.updateConfig(configReqVO);
        return success(true);
    }

    @GetMapping("/config")
    @Operation(summary = "获取项目配置")
    @Parameter(name = "projectId", description = "项目ID", required = true)
    @PreAuthorize("@ss.hasPermission('project:info:query')")
    public CommonResult<ProjectConfigRespVO> getConfig(@RequestParam("projectId") Long projectId) {
        return success(infoService.getConfig(projectId));
    }

    @PutMapping("/submitRunAudit")
    @Operation(summary = "提交运行审核")
    @PreAuthorize("@ss.hasPermission('project:info:update')")
    public CommonResult<Boolean> submitRunAudit(@Valid @RequestBody SubmitAuditReqVO submitReqVO) {
        infoService.submitRunAudit(submitReqVO);
        return success(true);
    }

    @PutMapping("/auditRun")
    @Operation(summary = "审核运行")
    @PreAuthorize("@ss.hasPermission('project:info:audit')")
    public CommonResult<Boolean> auditRun(@Valid @RequestBody ProjectRunAuditReqVO auditReqVO) {
        // 获取当前登录用户信息
        Long auditUserId = SecurityFrameworkUtils.getLoginUserId();
        String auditUserName = SecurityFrameworkUtils.getLoginUserNickname();
        infoService.auditRun(auditReqVO, auditUserId, auditUserName);
        return success(true);
    }

    @PutMapping("/endRun")
    @Operation(summary = "结束运行")
    @PreAuthorize("@ss.hasPermission('project:info:end')")
    public CommonResult<Boolean> endRun(@RequestParam("projectId") Long projectId) {
        infoService.endRun(projectId);
        return success(true);
    }

}
