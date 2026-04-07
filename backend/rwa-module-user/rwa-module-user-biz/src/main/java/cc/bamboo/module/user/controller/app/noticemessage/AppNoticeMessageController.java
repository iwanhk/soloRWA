package cc.bamboo.module.user.controller.app.noticemessage;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.module.user.controller.app.noticemessage.vo.AppNoticeMessagePageReqVO;
import cc.bamboo.module.user.controller.app.noticemessage.vo.AppNoticeMessageReadReqVO;
import cc.bamboo.module.user.controller.app.noticemessage.vo.AppNoticeMessageRespVO;
import cc.bamboo.module.user.dal.dataobject.noticemessage.NoticeMessageDO;
import cc.bamboo.module.user.service.noticemessage.AppNoticeMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cc.bamboo.framework.common.pojo.CommonResult.success;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 APP - 消息
 *
 * @author Kiro
 */
@Tag(name = "用户 APP - 消息")
@RestController
@RequestMapping("/user/notice-message")
@Validated
@Slf4j
public class AppNoticeMessageController {

    @Resource
    private AppNoticeMessageService appNoticeMessageService;

    @GetMapping("/page")
    @Operation(summary = "获取消息分页")
    public CommonResult<PageResult<AppNoticeMessageRespVO>> getMessagePage(@Valid AppNoticeMessagePageReqVO pageReqVO) {
        PageResult<NoticeMessageDO> pageResult = appNoticeMessageService.getMessagePage(getLoginUserId(), pageReqVO);
        return success(BeanUtils.toBean(pageResult, AppNoticeMessageRespVO.class));
    }

    @GetMapping("/detail")
    @Operation(summary = "根据ID获取消息详情")
    public CommonResult<AppNoticeMessageRespVO> getMessageById(@RequestParam("id") Long noticeMessageId) {
        return success(appNoticeMessageService.getMessageById(getLoginUserId(), noticeMessageId));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "获取未读消息数量")
    public CommonResult<Long> getUnreadCount() {
        return success(appNoticeMessageService.getUnreadCount(getLoginUserId()));
    }

    @PutMapping("/mark-read")
    @Operation(summary = "标记消息为已读")
    public CommonResult<Boolean> markAsRead(@Valid @RequestBody AppNoticeMessageReadReqVO reqVO) {
        appNoticeMessageService.markMessageAsRead(getLoginUserId(), reqVO.getId());
        return success(true);
    }

    @PutMapping("/mark-all-read")
    @Operation(summary = "标记所有消息为已读")
    public CommonResult<Boolean> markAllAsRead() {
        appNoticeMessageService.markAllAsRead(getLoginUserId());
        return success(true);
    }

}
