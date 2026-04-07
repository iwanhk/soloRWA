package cc.bamboo.module.user.service.noticemessage;

import cc.bamboo.framework.common.exception.ErrorCode;
import cc.bamboo.module.user.dal.dataobject.noticemessage.NoticeMessageDO;
import cc.bamboo.module.user.dal.dataobject.noticetemplate.NoticeTemplateDO;
import cc.bamboo.module.user.dal.mysql.noticemessage.NoticeMessageMapper;
import cc.bamboo.module.user.enums.notice.NoticeTemplateEnum;
import cc.bamboo.module.user.enums.notice.NoticeTypeEnum;
import cc.bamboo.module.user.service.noticetemplate.NoticeTemplateService;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Map;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 消息发送 Service 实现类
 *
 * @author Kiro
 */
@Service
@Validated
@Slf4j
public class NoticeMessageSendServiceImpl implements NoticeMessageSendService {

    @Resource
    private NoticeMessageMapper noticeMessageMapper;

    @Resource
    private NoticeTemplateService noticeTemplateService;

    @Override
    public Long sendSingleMessage(Long userId, String templateCode, Map<String, Object> templateParams) {
        NoticeTemplateEnum noticeTemplateEnum = NoticeTemplateEnum.getByCode(templateCode);
        // 1. 获取模板
        NoticeTemplateDO template = noticeTemplateService.getNoticeTemplateByCode(templateCode);
        if (template == null) {
            throw exception(new ErrorCode(50003, "消息模板不存在"));
        }

        // 2. 格式化内容和标题
        String content = noticeTemplateService.formatNoticeTemplateContent(template.getContent(), templateParams);
        String title = noticeTemplateService.formatNoticeTemplateContent(template.getTitle(), templateParams);

        // 3. 创建消息
        NoticeMessageDO message = NoticeMessageDO.builder()
                .userId(userId)
                .noticeType(noticeTemplateEnum.getType())
                .templateId(template.getId())
                .templateCode(template.getCode())
                .templateNickname(template.getNickname())
                .templateContent(content)
                .templateType(template.getType())
                .templateParams(JSONUtil.toJsonStr(templateParams))
                .templateTitle(title)
                .readStatus(false)
                .build();

        noticeMessageMapper.insert(message);
        return message.getId();
    }

    @Override
    public Long sendSystemMessage(String templateCode, Map<String, Object> templateParams) {
        // 1. 获取模板
        NoticeTemplateDO template = noticeTemplateService.getNoticeTemplateByCode(templateCode);
        if (template == null) {
            throw exception(new cc.bamboo.framework.common.exception.ErrorCode(50003, "消息模板不存在"));
        }

        // 2. 格式化内容
        String content = noticeTemplateService.formatNoticeTemplateContent(template.getContent(), templateParams);

        // 3. 创建系统消息（userId为null表示系统消息）
        NoticeMessageDO message = NoticeMessageDO.builder()
                .userId(null) // 系统消息不指定用户
                .noticeType(NoticeTypeEnum.SYSTEM.getType())
                .templateId(template.getId())
                .templateCode(template.getCode())
                .templateNickname(template.getNickname())
                .templateContent(content)
                .templateType(template.getType())
                .templateParams(JSONUtil.toJsonStr(templateParams))
                .readStatus(false)
                .build();

        noticeMessageMapper.insert(message);
        return message.getId();
    }

    @Override
    public Long sendOrderMessage(Long userId, Long orderId, String templateCode, Map<String, Object> templateParams) {
        // 1. 获取模板
        NoticeTemplateDO template = noticeTemplateService.getNoticeTemplateByCode(templateCode);
        if (template == null) {
            throw exception(new cc.bamboo.framework.common.exception.ErrorCode(50003, "消息模板不存在"));
        }

        // 2. 格式化内容
        String content = noticeTemplateService.formatNoticeTemplateContent(template.getContent(), templateParams);
        String title = noticeTemplateService.formatNoticeTemplateContent(template.getTitle(), templateParams);
        // 3. 创建订单消息
        NoticeMessageDO message = NoticeMessageDO.builder()
                .userId(userId)
                .noticeType(NoticeTypeEnum.PERSONAL.getType())
                .templateId(template.getId())
                .templateCode(template.getCode())
                .templateNickname(template.getNickname())
                .templateContent(content)
                .templateType(template.getType())
                .templateParams(JSONUtil.toJsonStr(templateParams))
                .templateTitle(title)
                .orderId(orderId)
                .noticeUrl("/order/detail/" + orderId)
                .readStatus(false)
                .build();

        noticeMessageMapper.insert(message);
        return message.getId();
    }

    @Override
    @Async
    public void sendSingleMessageAsync(Long userId, String templateCode, Map<String, Object> templateParams) {
        try {
            sendSingleMessage(userId, templateCode, templateParams);
            log.info("异步发送个人消息成功, userId={}, templateCode={}", userId, templateCode);
        } catch (Exception e) {
            log.error("异步发送个人消息失败, userId={}, templateCode={}", userId, templateCode, e);
        }
    }

    @Override
    @Async
    public void sendSystemMessageAsync(String templateCode, Map<String, Object> templateParams) {
        try {
            sendSystemMessage(templateCode, templateParams);
            log.info("异步发送系统消息成功, templateCode={}", templateCode);
        } catch (Exception e) {
            log.error("异步发送系统消息失败, templateCode={}", templateCode, e);
        }
    }

    @Override
    @Async
    public void sendOrderMessageAsync(Long userId, Long orderId, String templateCode, Map<String, Object> templateParams) {
        try {
            sendOrderMessage(userId, orderId, templateCode, templateParams);
            log.info("异步发送订单消息成功, userId={}, orderId={}, templateCode={}", userId, orderId, templateCode);
        } catch (Exception e) {
            log.error("异步发送订单消息失败, userId={}, orderId={}, templateCode={}", userId, orderId, templateCode, e);
        }
    }

}
