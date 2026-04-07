package cc.bamboo.module.user.mq.producer;


import cc.bamboo.module.user.mq.message.UserCreateMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户创建Identity生产者
 * 发送消息通知 chain 模块添加用户并签发 Claim
 *
 * @author Swolf
 */
@Component
@Slf4j
public class UserIdentityCreateProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "user.create.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "user.create";

    /**
     * 发送用户创建Identity
     *
     * @param message 用户创建Identity
     */
    public void sendUserCreateMessage(UserCreateMessage message) {
        try {
            log.info("[UserIdentityCreateProducer] 开始发送用户创建Identity，taskNo: {}, 用户ID: {}",
                    message.getTaskNo(),  message.getUserId());

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);

            log.info("[UserIdentityCreateProducer] 用户创建Identity发送成功，taskNo: {}, 用户ID: {}",
                    message.getTaskNo(),  message.getUserId());

        } catch (Exception e) {
            log.error("[UserIdentityCreateProducer] 用户创建Identity发送失败，taskNo: {}, 用户ID: {}, 错误信息: {}",
                    message.getTaskNo(),  message.getUserId(), e.getMessage(), e);
            // 抛出异常，让调用方知道消息发送失败
            throw new RuntimeException("用户创建Identity发送失败: " + e.getMessage(), e);
        }
    }

}
