package cc.bamboo.module.project.service.message;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.user.api.noticemessage.NoticeMessageApi;
import cc.bamboo.module.user.api.noticemessage.dto.NoticeMessageSendReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 项目模块 - 消息发送 Service 实现类
 *
 * @author Kiro
 */
@Service
@Slf4j
public class ProjectMessageServiceImpl implements ProjectMessageService {

    @Resource
    private NoticeMessageApi noticeMessageApi;

    @Override
    @Async
    public void sendSingleMessageAsync(Long userId, String templateCode, Map<String, Object> templateParams) {
        try {
            noticeMessageApi.sendSingleMessageAsync(new NoticeMessageSendReqDTO()
                    .setUserId(userId)
                    .setTemplateCode(templateCode)
                    .setTemplateParams(templateParams));
        } catch (Exception e) {
            log.error("[sendSingleMessageAsync][发送消息失败，userId={}, templateCode={}]", userId, templateCode, e);
        }
    }

    @Override
    @Async
    public void sendSystemMessageAsync(String templateCode, Map<String, Object> templateParams) {
        try {
            noticeMessageApi.sendSystemMessageAsync(new NoticeMessageSendReqDTO()
                    .setTemplateCode(templateCode)
                    .setTemplateParams(templateParams));
        } catch (Exception e) {
            log.error("[sendSystemMessageAsync][发送系统消息失败，templateCode={}]", templateCode, e);
        }
    }

    @Override
    @Async
    public void sendOrderMessageAsync(Long userId, Long orderId, String templateCode,
            Map<String, Object> templateParams) {
        try {
            CommonResult<Boolean> commonResult = noticeMessageApi.sendOrderMessageAsync(new NoticeMessageSendReqDTO()
                    .setUserId(userId)
                    .setOrderId(orderId)
                    .setTemplateCode(templateCode)
                    .setTemplateParams(templateParams));
            if (!commonResult.isSuccess()) {
                log.error("[sendOrderMessageAsync][发送订单消息失败，userId={}, orderId={}, templateCode={}, message={}]",
                        userId, orderId, templateCode, commonResult.getMsg());
            }
        } catch (Exception e) {
            log.error("[sendOrderMessageAsync][发送订单消息失败，userId={}, orderId={}, templateCode={}]", userId, orderId,
                    templateCode, e);
        }
    }

}
