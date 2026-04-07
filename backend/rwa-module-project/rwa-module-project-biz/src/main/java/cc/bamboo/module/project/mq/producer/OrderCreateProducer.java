package cc.bamboo.module.project.mq.producer;

import cc.bamboo.module.project.mq.message.OrderCreateMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户下单消息生产者
 * 发送消息通知 chain 模块添加用户并签发 Claim
 *
 * @author Swolf
 */
@Component
@Slf4j
public class OrderCreateProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "order.create.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "order.create";

    /**
     * 发送用户下单消息
     *
     * @param message 用户下单消息
     */
    public void sendOrderCreateMessage(OrderCreateMessage message) {
        try {
            log.info("[OrderCreateProducer] 开始发送用户下单消息，taskNo: {}, 订单ID: {}, 用户ID: {}",
                    message.getTaskNo(), message.getOrderId(), message.getUserId());

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);

            log.info("[OrderCreateProducer] 用户下单消息发送成功，taskNo: {}, 订单ID: {}, 用户ID: {}",
                    message.getTaskNo(), message.getOrderId(), message.getUserId());

        } catch (Exception e) {
            log.error("[OrderCreateProducer] 用户下单消息发送失败，taskNo: {}, 订单ID: {}, 用户ID: {}, 错误信息: {}",
                    message.getTaskNo(), message.getOrderId(), message.getUserId(), e.getMessage(), e);
            // 抛出异常，让调用方知道消息发送失败
            throw new RuntimeException("用户下单消息发送失败: " + e.getMessage(), e);
        }
    }

}
