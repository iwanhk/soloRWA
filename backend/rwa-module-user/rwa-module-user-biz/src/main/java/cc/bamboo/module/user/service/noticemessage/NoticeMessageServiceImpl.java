package cc.bamboo.module.user.service.noticemessage;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.user.controller.admin.noticemessage.vo.*;
import cc.bamboo.module.user.dal.dataobject.noticemessage.NoticeMessageDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.user.dal.mysql.noticemessage.NoticeMessageMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.*;

/**
 * 用户消息 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class NoticeMessageServiceImpl implements NoticeMessageService {

    @Resource
    private NoticeMessageMapper noticeMessageMapper;

    @Override
    public Long createNoticeMessage(NoticeMessageSaveReqVO createReqVO) {
        // 插入
        NoticeMessageDO noticeMessage = BeanUtils.toBean(createReqVO, NoticeMessageDO.class);
        noticeMessageMapper.insert(noticeMessage);
        // 返回
        return noticeMessage.getId();
    }

    @Override
    public void updateNoticeMessage(NoticeMessageSaveReqVO updateReqVO) {
        // 校验存在
        validateNoticeMessageExists(updateReqVO.getId());
        // 更新
        NoticeMessageDO updateObj = BeanUtils.toBean(updateReqVO, NoticeMessageDO.class);
        noticeMessageMapper.updateById(updateObj);
    }

    @Override
    public void deleteNoticeMessage(Long id) {
        // 校验存在
        validateNoticeMessageExists(id);
        // 删除
        noticeMessageMapper.deleteById(id);
    }

    private void validateNoticeMessageExists(Long id) {
        if (noticeMessageMapper.selectById(id) == null) {
            throw exception(NOTICE_MESSAGE_NOT_EXISTS);
        }
    }

    @Override
    public NoticeMessageDO getNoticeMessage(Long id) {
        return noticeMessageMapper.selectById(id);
    }

    @Override
    public PageResult<NoticeMessageDO> getNoticeMessagePage(NoticeMessagePageReqVO pageReqVO) {
        return noticeMessageMapper.selectPage(pageReqVO);
    }

}