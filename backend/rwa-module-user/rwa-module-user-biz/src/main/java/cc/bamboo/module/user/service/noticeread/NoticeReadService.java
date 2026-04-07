package cc.bamboo.module.user.service.noticeread;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.user.controller.admin.noticeread.vo.*;
import cc.bamboo.module.user.dal.dataobject.noticeread.NoticeReadDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 系统消息已读记录 Service 接口
 *
 * @author Swolf
 */
public interface NoticeReadService {

    /**
     * 创建系统消息已读记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createNoticeRead(@Valid NoticeReadSaveReqVO createReqVO);

    /**
     * 更新系统消息已读记录
     *
     * @param updateReqVO 更新信息
     */
    void updateNoticeRead(@Valid NoticeReadSaveReqVO updateReqVO);

    /**
     * 删除系统消息已读记录
     *
     * @param id 编号
     */
    void deleteNoticeRead(Long id);

    /**
     * 获得系统消息已读记录
     *
     * @param id 编号
     * @return 系统消息已读记录
     */
    NoticeReadDO getNoticeRead(Long id);

    /**
     * 获得系统消息已读记录分页
     *
     * @param pageReqVO 分页查询
     * @return 系统消息已读记录分页
     */
    PageResult<NoticeReadDO> getNoticeReadPage(NoticeReadPageReqVO pageReqVO);

}