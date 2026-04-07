package cc.bamboo.module.user.service.noticeread;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.user.controller.admin.noticeread.vo.*;
import cc.bamboo.module.user.dal.dataobject.noticeread.NoticeReadDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.user.dal.mysql.noticeread.NoticeReadMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.*;

/**
 * 系统消息已读记录 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class NoticeReadServiceImpl implements NoticeReadService {

    @Resource
    private NoticeReadMapper noticeReadMapper;

    @Override
    public Long createNoticeRead(NoticeReadSaveReqVO createReqVO) {
        // 插入
        NoticeReadDO noticeRead = BeanUtils.toBean(createReqVO, NoticeReadDO.class);
        noticeReadMapper.insert(noticeRead);
        // 返回
        return noticeRead.getId();
    }

    @Override
    public void updateNoticeRead(NoticeReadSaveReqVO updateReqVO) {
        // 校验存在
        validateNoticeReadExists(updateReqVO.getId());
        // 更新
        NoticeReadDO updateObj = BeanUtils.toBean(updateReqVO, NoticeReadDO.class);
        noticeReadMapper.updateById(updateObj);
    }

    @Override
    public void deleteNoticeRead(Long id) {
        // 校验存在
        validateNoticeReadExists(id);
        // 删除
        noticeReadMapper.deleteById(id);
    }

    private void validateNoticeReadExists(Long id) {
        if (noticeReadMapper.selectById(id) == null) {
            throw exception(NOTICE_READ_NOT_EXISTS);
        }
    }

    @Override
    public NoticeReadDO getNoticeRead(Long id) {
        return noticeReadMapper.selectById(id);
    }

    @Override
    public PageResult<NoticeReadDO> getNoticeReadPage(NoticeReadPageReqVO pageReqVO) {
        return noticeReadMapper.selectPage(pageReqVO);
    }

}