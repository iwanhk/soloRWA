package cc.bamboo.module.user.service.noticemessage;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.user.controller.admin.noticemessage.vo.*;
import cc.bamboo.module.user.dal.dataobject.noticemessage.NoticeMessageDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 用户消息 Service 接口
 *
 * @author Swolf
 */
public interface NoticeMessageService {

    /**
     * 创建用户消息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createNoticeMessage(@Valid NoticeMessageSaveReqVO createReqVO);

    /**
     * 更新用户消息
     *
     * @param updateReqVO 更新信息
     */
    void updateNoticeMessage(@Valid NoticeMessageSaveReqVO updateReqVO);

    /**
     * 删除用户消息
     *
     * @param id 编号
     */
    void deleteNoticeMessage(Long id);

    /**
     * 获得用户消息
     *
     * @param id 编号
     * @return 用户消息
     */
    NoticeMessageDO getNoticeMessage(Long id);

    /**
     * 获得用户消息分页
     *
     * @param pageReqVO 分页查询
     * @return 用户消息分页
     */
    PageResult<NoticeMessageDO> getNoticeMessagePage(NoticeMessagePageReqVO pageReqVO);

}