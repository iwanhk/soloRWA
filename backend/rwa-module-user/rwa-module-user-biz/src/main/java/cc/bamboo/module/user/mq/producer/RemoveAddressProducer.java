package cc.bamboo.module.user.mq.producer;


import cc.bamboo.module.user.mq.message.RemoveAddressMessage;
import cc.bamboo.module.user.mq.message.UserClaimMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 解绑生产者
 * 发送消息通知 chain 模块添加用户并解绑 Claim
 *
 * @author Swolf
 */
@Component
@Slf4j
public class RemoveAddressProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "user.remove.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "user.remove";

    /**
     * 发送解绑
     *
     * @param message 解绑
     */
    public void sendRemoveAddressMessage(RemoveAddressMessage message) {
        try {
            log.info("[RemoveAddressProducer] 开始发送解绑，taskNo: {}, 用户ID: {}",
                    message.getTaskNo(),  message.getUserId());

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);

            log.info("[RemoveAddressProducer] 解绑发送成功，taskNo: {}, 用户ID: {}",
                    message.getTaskNo(),  message.getUserId());

        } catch (Exception e) {
            log.error("[RemoveAddressProducer] 解绑发送失败，taskNo: {}, 用户ID: {}, 错误信息: {}",
                    message.getTaskNo(),  message.getUserId(), e.getMessage(), e);
            // 抛出异常，让调用方知道消息发送失败
            throw new RuntimeException("解绑发送失败: " + e.getMessage(), e);
        }
    }

}
