package cc.bamboo.module.project.mq.producer;

import cc.bamboo.module.project.mq.message.OrderAuditPassMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 订单审核通过消息生产者
 * 发送消息通知 chain 模块为用户铸造 Token
 *
 * @author Swolf
 */
@Component
@Slf4j
public class OrderAuditPassProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "order.audit.pass.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "order.audit.pass";

    /**
     * 发送订单审核通过消息
     *
     * @param message 订单审核通过消息
     */
    public void sendOrderAuditPassMessage(OrderAuditPassMessage message) {
        try {
            log.info("[OrderAuditPassProducer] 开始发送订单审核通过消息，taskNo: {}, 订单ID: {}, 用户ID: {}",
                    message.getTaskNo(), message.getOrderId(), message.getUserId());

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);

            log.info("[OrderAuditPassProducer] 订单审核通过消息发送成功，taskNo: {}, 订单ID: {}, 用户ID: {}",
                    message.getTaskNo(), message.getOrderId(), message.getUserId());

        } catch (Exception e) {
            log.error("[OrderAuditPassProducer] 订单审核通过消息发送失败，taskNo: {}, 订单ID: {}, 用户ID: {}, 错误信息: {}",
                    message.getTaskNo(), message.getOrderId(), message.getUserId(), e.getMessage(), e);
            // 抛出异常，让调用方知道消息发送失败
            throw new RuntimeException("订单审核通过消息发送失败: " + e.getMessage(), e);
        }
    }

}
