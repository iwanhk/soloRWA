package cc.bamboo.module.user.service.noticemessage;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.module.user.controller.app.noticemessage.vo.AppNoticeMessagePageReqVO;
import cc.bamboo.module.user.controller.app.noticemessage.vo.AppNoticeMessageRespVO;
import cc.bamboo.module.user.dal.dataobject.noticemessage.NoticeMessageDO;

/**
 * APP端消息 Service 接口
 *
 * @author Kiro
 */
public interface AppNoticeMessageService {

    /**
     * 获取用户消息分页
     *
     * @param userId 用户ID
     * @param pageReqVO 分页请求
     * @return 消息分页
     */
    PageResult<NoticeMessageDO> getMessagePage(Long userId, AppNoticeMessagePageReqVO pageReqVO);

    /**
     * 根据ID获取消息详情
     *
     * @param userId 用户ID
     * @param noticeMessageId 消息ID
     * @return 消息详情
     */
    AppNoticeMessageRespVO getMessageById(Long userId, Long noticeMessageId);
    /**
     * 获取用户未读消息数量
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 标记消息为已读（普通消息）
     *
     * @param userId 用户ID
     * @param messageId 消息ID
     */
    void markMessageAsRead(Long userId, Long messageId);

    /**
     * 标记系统消息为已读
     *
     * @param userId 用户ID
     * @param messageId 消息ID
     */
    void markSystemMessageAsRead(Long userId, Long messageId);

    /**
     * 标记所有消息为已读
     *
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);

}
