package cc.bamboo.module.project.controller.admin.projectnotice;

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

import cc.bamboo.module.project.controller.admin.projectnotice.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectnotice.ProjectNoticeDO;
import cc.bamboo.module.project.service.projectnotice.ProjectNoticeService;

@Tag(name = "管理后台 - 项目通告表（含全局通告）")
@RestController
@RequestMapping("/project/notice")
@Validated
public class ProjectNoticeController {

    @Resource
    private ProjectNoticeService noticeService;

    @PostMapping("/create")
    @Operation(summary = "创建项目通告表（含全局通告）")
    @PreAuthorize("@ss.hasPermission('project:notice:create')")
    public CommonResult<Long> createNotice(@Valid @RequestBody ProjectNoticeSaveReqVO createReqVO) {
        return success(noticeService.createNotice(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新项目通告表（含全局通告）")
    @PreAuthorize("@ss.hasPermission('project:notice:update')")
    public CommonResult<Boolean> updateNotice(@Valid @RequestBody ProjectNoticeUpdateReqVO updateReqVO) {
        noticeService.updateNotice(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除项目通告表（含全局通告）")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:notice:delete')")
    public CommonResult<Boolean> deleteNotice(@RequestParam("id") Long id) {
        noticeService.deleteNotice(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得项目通告表（含全局通告）")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:notice:query')")
    public CommonResult<ProjectNoticeRespVO> getNotice(@RequestParam("id") Long id) {
        ProjectNoticeDO notice = noticeService.getNotice(id);
        return success(BeanUtils.toBean(notice, ProjectNoticeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得项目通告表（含全局通告）分页")
    @PreAuthorize("@ss.hasPermission('project:notice:query')")
    public CommonResult<PageResult<ProjectNoticeRespVO>> getNoticePage(@Valid ProjectNoticePageReqVO pageReqVO) {
        PageResult<ProjectNoticeDO> pageResult = noticeService.getNoticePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProjectNoticeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出项目通告表（含全局通告） Excel")
    @PreAuthorize("@ss.hasPermission('project:notice:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportNoticeExcel(@Valid ProjectNoticePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProjectNoticeDO> list = noticeService.getNoticePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "项目通告表（含全局通告）.xls", "数据", ProjectNoticeRespVO.class,
                        BeanUtils.toBean(list, ProjectNoticeRespVO.class));
    }

}