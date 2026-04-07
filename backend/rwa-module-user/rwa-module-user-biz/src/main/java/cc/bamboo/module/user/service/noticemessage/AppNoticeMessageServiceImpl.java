package cc.bamboo.module.user.service.noticemessage;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.module.user.controller.app.noticemessage.vo.AppNoticeMessagePageReqVO;
import cc.bamboo.module.user.controller.app.noticemessage.vo.AppNoticeMessageRespVO;
import cc.bamboo.module.user.dal.dataobject.noticemessage.NoticeMessageDO;
import cc.bamboo.module.user.dal.dataobject.noticeread.NoticeReadDO;
import cc.bamboo.module.user.dal.dataobject.userinfo.UserInfoDO;
import cc.bamboo.module.user.dal.mysql.noticemessage.NoticeMessageMapper;
import cc.bamboo.module.user.dal.mysql.noticeread.NoticeReadMapper;
import cc.bamboo.module.user.dal.mysql.userinfo.UserInfoMapper;
import cc.bamboo.module.user.enums.notice.NoticeTypeEnum;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.*;

/**
 * APP端消息 Service 实现类
 *
 * @author Kiro
 */
@Service
@Validated
@Slf4j
public class AppNoticeMessageServiceImpl implements AppNoticeMessageService {

    @Resource
    private NoticeMessageMapper noticeMessageMapper;

    @Resource
    private NoticeReadMapper noticeReadMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public PageResult<NoticeMessageDO> getMessagePage(Long userId, AppNoticeMessagePageReqVO pageReqVO) {
        LocalDateTime userRegisterTime = null;
        if(pageReqVO.getNoticeType() == null){
            UserInfoDO userInfoDO =  userInfoMapper.selectById(userId);
            userRegisterTime = userInfoDO.getCreateTime();
        }
        // 1. 查询消息分页
        PageResult<NoticeMessageDO> pageResult = noticeMessageMapper.selectPage(userId, pageReqVO,userRegisterTime);
        
        // 2. 处理系统消息的已读状态
        if (!pageResult.getList().isEmpty()) {
            // 筛选出系统消息
            List<NoticeMessageDO> systemMessages = pageResult.getList().stream()
                    .filter(msg -> NoticeTypeEnum.SYSTEM.getType().equals(msg.getNoticeType()))
                    .collect(Collectors.toList());
            
            if (!systemMessages.isEmpty()) {
                // 批量查询该用户对这些系统消息的已读状态
                List<Long> systemMessageIds = systemMessages.stream()
                        .map(NoticeMessageDO::getId)
                        .collect(Collectors.toList());
                
                List<NoticeReadDO> readRecords = noticeReadMapper.selectList(new LambdaQueryWrapper<NoticeReadDO>()
                        .eq(NoticeReadDO::getUserId, userId)
                        .in(NoticeReadDO::getNoticeId, systemMessageIds));
                
                // 构建已读记录Map，key为noticeId
                Map<Long, NoticeReadDO> readMap = readRecords.stream()
                        .collect(Collectors.toMap(NoticeReadDO::getNoticeId, r -> r));
                
                // 更新系统消息的已读状态
                for (NoticeMessageDO message : systemMessages) {
                    NoticeReadDO readRecord = readMap.get(message.getId());
                    if (readRecord != null && readRecord.getReadStatus() == 1) {
                        message.setReadStatus(true);
                        message.setReadTime(readRecord.getReadTime());
                    } else {
                        message.setReadStatus(false);
                        message.setReadTime(null);
                    }
                }
            }
        }
        
        return pageResult;
    }

    @Override
    public AppNoticeMessageRespVO getMessageById(Long userId, Long noticeMessageId) {
        // 1. 查询消息
        NoticeMessageDO noticeMessageDO = noticeMessageMapper.selectById(noticeMessageId);
        if (noticeMessageDO == null) {
            return null;
        }

        // 2. 检查是否是该用户的消息
        if (!noticeMessageDO.getNoticeType().equals(NoticeTypeEnum.SYSTEM.getType()) && !noticeMessageDO.getUserId().equals(userId)) {
            return null;
        }
                // 3. 构建响应VO
        AppNoticeMessageRespVO respVO = BeanUtil.copyProperties(noticeMessageDO, AppNoticeMessageRespVO.class);
        respVO.setNoticeType(noticeMessageDO.getNoticeType());

        //如果是系统消息需要查询已读状态
        if (NoticeTypeEnum.SYSTEM.getType().equals(respVO.getNoticeType())) {
            // 查询已读状态
            NoticeReadDO readRecord = noticeReadMapper.selectOne(new LambdaQueryWrapper<NoticeReadDO>()
                    .eq(NoticeReadDO::getUserId, userId)
                    .eq(NoticeReadDO::getNoticeId, noticeMessageId));

            if (readRecord != null && readRecord.getReadStatus() == 1) {
                respVO.setReadStatus(true);
                respVO.setReadTime(readRecord.getReadTime());
            } else {
                respVO.setReadStatus(false);
                respVO.setReadTime(null);
            }
        }

        //如果是未读，则设为已读
        if (!respVO.getReadStatus()){
            // 2. 如果是系统消息，使用系统消息已读逻辑
            if (NoticeTypeEnum.SYSTEM.getType().equals(noticeMessageDO.getNoticeType())) {
                markSystemMessageAsRead(userId, noticeMessageId);
            }else{
                NoticeMessageDO updateObj = new NoticeMessageDO();
                updateObj.setId(noticeMessageId);
                updateObj.setReadStatus(true);
                updateObj.setReadTime(LocalDateTime.now());
                noticeMessageMapper.updateById(updateObj);
            }
        }

        return respVO;
    }

    @Override
    public Long getUnreadCount(Long userId) {
        // 1. 查询个人消息的未读数量
        Long personalUnreadCount = noticeMessageMapper.selectCount(new LambdaQueryWrapper<NoticeMessageDO>()
                .eq(NoticeMessageDO::getUserId, userId)
                .eq(NoticeMessageDO::getReadStatus, false));
        
        // 2. 查询系统消息的未读数量
        // 先查询所有系统消息
        List<NoticeMessageDO> systemMessages = noticeMessageMapper.selectList(new LambdaQueryWrapper<NoticeMessageDO>()
                .isNull(NoticeMessageDO::getUserId)
                .eq(NoticeMessageDO::getNoticeType, NoticeTypeEnum.SYSTEM.getType()));
        
        if (systemMessages.isEmpty()) {
            return personalUnreadCount;
        }
        
        // 查询该用户对系统消息的已读记录
        List<Long> systemMessageIds = systemMessages.stream()
                .map(NoticeMessageDO::getId)
                .collect(Collectors.toList());
        
        List<NoticeReadDO> readRecords = noticeReadMapper.selectList(new LambdaQueryWrapper<NoticeReadDO>()
                .eq(NoticeReadDO::getUserId, userId)
                .in(NoticeReadDO::getNoticeId, systemMessageIds)
                .eq(NoticeReadDO::getReadStatus, 1));
        
        // 已读的系统消息ID集合
        List<Long> readSystemMessageIds = readRecords.stream()
                .map(NoticeReadDO::getNoticeId)
                .collect(Collectors.toList());
        
        // 未读的系统消息数量 = 总系统消息数 - 已读的系统消息数
        long systemUnreadCount = systemMessages.size() - readSystemMessageIds.size();
        
        return personalUnreadCount + systemUnreadCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMessageAsRead(Long userId, Long messageId) {
        // 1. 校验消息是否存在且属于该用户
        NoticeMessageDO message = noticeMessageMapper.selectById(messageId);
        if (message == null) {
            throw exception(new cc.bamboo.framework.common.exception.ErrorCode(50001, "消息不存在"));
        }
        if (!message.getUserId().equals(userId)) {
            throw exception(new cc.bamboo.framework.common.exception.ErrorCode(50002, "无权操作该消息"));
        }

        // 2. 如果是系统消息，使用系统消息已读逻辑
        if (NoticeTypeEnum.SYSTEM.getType().equals(message.getNoticeType())) {
            markSystemMessageAsRead(userId, messageId);
            return;
        }

        // 3. 普通消息直接标记已读
        if (!message.getReadStatus()) {
            NoticeMessageDO updateObj = new NoticeMessageDO();
            updateObj.setId(messageId);
            updateObj.setReadStatus(true);
            updateObj.setReadTime(LocalDateTime.now());
            noticeMessageMapper.updateById(updateObj);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markSystemMessageAsRead(Long userId, Long messageId) {
        // 1. 校验消息是否存在
        NoticeMessageDO message = noticeMessageMapper.selectById(messageId);
        if (message == null) {
            throw exception(new cc.bamboo.framework.common.exception.ErrorCode(50001, "消息不存在"));
        }

        // 2. 检查是否已经有已读记录
        NoticeReadDO existRead = noticeReadMapper.selectByNoticeIdAndUserId(messageId, userId);
        if (existRead != null) {
            // 如果已存在记录，更新为已读
            if (existRead.getReadStatus() == 0) {
                NoticeReadDO updateObj = new NoticeReadDO();
                updateObj.setId(existRead.getId());
                updateObj.setReadStatus(1);
                updateObj.setReadTime(LocalDateTime.now());
                noticeReadMapper.updateById(updateObj);
            }
        } else {
            // 创建新的已读记录
            NoticeReadDO readRecord = NoticeReadDO.builder()
                    .noticeId(messageId)
                    .userId(userId)
                    .readStatus(1)
                    .readTime(LocalDateTime.now())
                    .build();
            noticeReadMapper.insert(readRecord);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        // 标记所有未读消息为已读
        noticeMessageMapper.update(null, new LambdaUpdateWrapper<NoticeMessageDO>()
                .eq(NoticeMessageDO::getUserId, userId)
                .eq(NoticeMessageDO::getReadStatus, false)
                .set(NoticeMessageDO::getReadStatus, true)
                .set(NoticeMessageDO::getReadTime, LocalDateTime.now()));
    }

}
