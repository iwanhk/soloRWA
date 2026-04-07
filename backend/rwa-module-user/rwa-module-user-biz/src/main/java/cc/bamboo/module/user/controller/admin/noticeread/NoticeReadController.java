package cc.bamboo.module.user.controller.admin.noticeread;

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

import cc.bamboo.module.user.controller.admin.noticeread.vo.*;
import cc.bamboo.module.user.dal.dataobject.noticeread.NoticeReadDO;
import cc.bamboo.module.user.service.noticeread.NoticeReadService;

@Tag(name = "管理后台 - 系统消息已读记录")
@RestController
@RequestMapping("/user/notice-read")
@Validated
public class NoticeReadController {

    @Resource
    private NoticeReadService noticeReadService;

    @PostMapping("/create")
    @Operation(summary = "创建系统消息已读记录")
    @PreAuthorize("@ss.hasPermission('user:notice-read:create')")
    public CommonResult<Long> createNoticeRead(@Valid @RequestBody NoticeReadSaveReqVO createReqVO) {
        return success(noticeReadService.createNoticeRead(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新系统消息已读记录")
    @PreAuthorize("@ss.hasPermission('user:notice-read:update')")
    public CommonResult<Boolean> updateNoticeRead(@Valid @RequestBody NoticeReadSaveReqVO updateReqVO) {
        noticeReadService.updateNoticeRead(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除系统消息已读记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('user:notice-read:delete')")
    public CommonResult<Boolean> deleteNoticeRead(@RequestParam("id") Long id) {
        noticeReadService.deleteNoticeRead(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得系统消息已读记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('user:notice-read:query')")
    public CommonResult<NoticeReadRespVO> getNoticeRead(@RequestParam("id") Long id) {
        NoticeReadDO noticeRead = noticeReadService.getNoticeRead(id);
        return success(BeanUtils.toBean(noticeRead, NoticeReadRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得系统消息已读记录分页")
    @PreAuthorize("@ss.hasPermission('user:notice-read:query')")
    public CommonResult<PageResult<NoticeReadRespVO>> getNoticeReadPage(@Valid NoticeReadPageReqVO pageReqVO) {
        PageResult<NoticeReadDO> pageResult = noticeReadService.getNoticeReadPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, NoticeReadRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出系统消息已读记录 Excel")
    @PreAuthorize("@ss.hasPermission('user:notice-read:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportNoticeReadExcel(@Valid NoticeReadPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<NoticeReadDO> list = noticeReadService.getNoticeReadPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "系统消息已读记录.xls", "数据", NoticeReadRespVO.class,
                        BeanUtils.toBean(list, NoticeReadRespVO.class));
    }

}