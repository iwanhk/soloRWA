package cc.bamboo.module.project.mq.producer;

import cc.bamboo.module.project.mq.message.OrderCancelMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 订单取消延时消息生产者
 * 发送延时消息用于30分钟后自动取消未支付订单
 *
 * @author Swolf
 */
@Component
@Slf4j
public class OrderCancelProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "project.order.cancel.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "project.order.cancel";

    /**
     * 延时时间：60分钟（单位：毫秒）
     */
   // private static final long DELAY_TIME = 24 * 60 * 60 * 1000;

    @Value("${order.delay-time}")
    private long DELAY_TIME ;
    /**
     * 发送订单取消延时消息
     *
     * @param message 订单取消消息
     */
    public void sendOrderCancelMessage(OrderCancelMessage message) {
        try {
            log.info("[OrderCancelProducer] 开始发送订单取消延时消息，订单ID: {}, 订单号: {}, 延时时间: {}分钟",
                    message.getOrderId(), message.getOrderNo(), DELAY_TIME / 60000);

            // 发送延时消息
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message msg) throws AmqpException {
                    // 设置消息延时时间（使用 x-delay 头）
                    msg.getMessageProperties().setDelay((int) DELAY_TIME);
                    return msg;
                }
            });

            log.info("[OrderCancelProducer] 订单取消延时消息发送成功，订单ID: {}, 订单号: {}",
                    message.getOrderId(), message.getOrderNo());

        } catch (Exception e) {
            log.error("[OrderCancelProducer] 订单取消延时消息发送失败，订单ID: {}, 订单号: {}, 错误信息: {}",
                    message.getOrderId(), message.getOrderNo(), e.getMessage(), e);
            // 不抛出异常，避免影响订单创建流程
        }
    }

}
