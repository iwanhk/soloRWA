package cc.bamboo.module.user.controller.admin.noticemessage;

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

import cc.bamboo.module.user.controller.admin.noticemessage.vo.*;
import cc.bamboo.module.user.dal.dataobject.noticemessage.NoticeMessageDO;
import cc.bamboo.module.user.service.noticemessage.NoticeMessageService;

@Tag(name = "管理后台 - 用户消息")
@RestController
@RequestMapping("/user/notice-message")
@Validated
public class NoticeMessageController {

    @Resource
    private NoticeMessageService noticeMessageService;

    @PostMapping("/create")
    @Operation(summary = "创建用户消息")
    @PreAuthorize("@ss.hasPermission('user:notice-message:create')")
    public CommonResult<Long> createNoticeMessage(@Valid @RequestBody NoticeMessageSaveReqVO createReqVO) {
        return success(noticeMessageService.createNoticeMessage(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户消息")
    @PreAuthorize("@ss.hasPermission('user:notice-message:update')")
    public CommonResult<Boolean> updateNoticeMessage(@Valid @RequestBody NoticeMessageSaveReqVO updateReqVO) {
        noticeMessageService.updateNoticeMessage(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户消息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('user:notice-message:delete')")
    public CommonResult<Boolean> deleteNoticeMessage(@RequestParam("id") Long id) {
        noticeMessageService.deleteNoticeMessage(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户消息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('user:notice-message:query')")
    public CommonResult<NoticeMessageRespVO> getNoticeMessage(@RequestParam("id") Long id) {
        NoticeMessageDO noticeMessage = noticeMessageService.getNoticeMessage(id);
        return success(BeanUtils.toBean(noticeMessage, NoticeMessageRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户消息分页")
    @PreAuthorize("@ss.hasPermission('user:notice-message:query')")
    public CommonResult<PageResult<NoticeMessageRespVO>> getNoticeMessagePage(@Valid NoticeMessagePageReqVO pageReqVO) {
        PageResult<NoticeMessageDO> pageResult = noticeMessageService.getNoticeMessagePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, NoticeMessageRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户消息 Excel")
    @PreAuthorize("@ss.hasPermission('user:notice-message:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportNoticeMessageExcel(@Valid NoticeMessagePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<NoticeMessageDO> list = noticeMessageService.getNoticeMessagePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户消息.xls", "数据", NoticeMessageRespVO.class,
                        BeanUtils.toBean(list, NoticeMessageRespVO.class));
    }

}