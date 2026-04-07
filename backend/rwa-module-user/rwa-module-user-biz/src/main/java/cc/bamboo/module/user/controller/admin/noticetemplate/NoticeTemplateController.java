package cc.bamboo.module.user.controller.admin.noticetemplate;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.module.user.controller.admin.noticetemplate.vo.NoticeTemplatePageReqVO;
import cc.bamboo.module.user.controller.admin.noticetemplate.vo.NoticeTemplateRespVO;
import cc.bamboo.module.user.controller.admin.noticetemplate.vo.NoticeTemplateSaveReqVO;
import cc.bamboo.module.user.controller.admin.noticetemplate.vo.NoticeTemplateSendReqVO;
import cc.bamboo.module.user.dal.dataobject.noticetemplate.NoticeTemplateDO;
import cc.bamboo.module.user.enums.notice.NoticeTypeEnum;
import cc.bamboo.module.user.service.noticemessage.NoticeMessageSendService;
import cc.bamboo.module.user.service.noticetemplate.NoticeTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 消息模板")
@RestController
@RequestMapping("/user/notice-template")
@Validated
public class NoticeTemplateController {

    @Resource
    private NoticeTemplateService noticeTemplateService;

    @Resource
    private NoticeMessageSendService noticeMessageSendService;

    @PostMapping("/create")
    @Operation(summary = "创建消息模板")
    @PreAuthorize("@ss.hasPermission('user:notice-template:create')")
    public CommonResult<Long> createNoticeTemplate(@Valid @RequestBody NoticeTemplateSaveReqVO createReqVO) {
        return success(noticeTemplateService.createNoticeTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新消息模板")
    @PreAuthorize("@ss.hasPermission('user:notice-template:update')")
    public CommonResult<Boolean> updateNoticeTemplate(@Valid @RequestBody NoticeTemplateSaveReqVO updateReqVO) {
        noticeTemplateService.updateNoticeTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除消息模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('user:notice-template:delete')")
    public CommonResult<Boolean> deleteNoticeTemplate(@RequestParam("id") Long id) {
        noticeTemplateService.deleteNoticeTemplate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得消息模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('user:notice-template:query')")
    public CommonResult<NoticeTemplateRespVO> getNoticeTemplate(@RequestParam("id") Long id) {
        NoticeTemplateDO template = noticeTemplateService.getNoticeTemplate(id);
        return success(BeanUtils.toBean(template, NoticeTemplateRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得消息模板分页")
    @PreAuthorize("@ss.hasPermission('user:notice-template:query')")
    public CommonResult<PageResult<NoticeTemplateRespVO>> getNoticeTemplatePage(@Valid NoticeTemplatePageReqVO pageVO) {
        PageResult<NoticeTemplateDO> pageResult = noticeTemplateService.getNoticeTemplatePage(pageVO);
        return success(BeanUtils.toBean(pageResult, NoticeTemplateRespVO.class));
    }

    @PostMapping("/send-message")
    @Operation(summary = "发送测试消息")
    @PreAuthorize("@ss.hasPermission('user:notice-template:send')")
    public CommonResult<Long> sendMessage(@Valid @RequestBody NoticeTemplateSendReqVO sendReqVO) {
        Long messageId;
        if (NoticeTypeEnum.SYSTEM.getType().equals(sendReqVO.getMessageType())) {
            // 发送系统消息
            messageId = noticeMessageSendService.sendSystemMessage(
                    sendReqVO.getTemplateCode(),
                    sendReqVO.getTemplateParams()
            );
        } else {
            // 发送个人消息
            if (sendReqVO.getUserId() == null) {
                throw new IllegalArgumentException("个人消息必须指定用户ID");
            }
            messageId = noticeMessageSendService.sendSingleMessage(
                    sendReqVO.getUserId(),
                    sendReqVO.getTemplateCode(),
                    sendReqVO.getTemplateParams()
            );
        }
        return success(messageId);
    }

}
