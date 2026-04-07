package cc.bamboo.module.project.mq.consumer;

import cc.bamboo.module.project.mq.message.OrderCancelMessage;
import cc.bamboo.module.project.service.projectorder.ProjectOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 订单取消延时消息消费者
 * 负责消费延时消息并调用Service层处理业务逻辑
 *
 * @author Swolf
 */
@Component
@Slf4j
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        name = "project.order.cancel.queue",
                        durable = "true"
                ),
                exchange = @Exchange(
                        name = "project.order.cancel.exchange",
                        type = ExchangeTypes.DIRECT,
                        delayed = "true", // 启用延时交换机
                        durable = "true"
                ),
                key = "project.order.cancel"
        )
)
public class OrderCancelConsumer {

    @Resource
    private ProjectOrderService projectOrderService;

    /**
     * 处理订单取消消息
     * Consumer只负责消费消息，具体业务逻辑由Service层实现
     *
     * @param message 订单取消消息
     */
    @RabbitHandler
    public void onMessage(OrderCancelMessage message) {
        log.info("[OrderCancelConsumer] 收到订单取消消息，订单ID: {}, 订单号: {}, 项目ID: {}, 数量: {}",
                message.getOrderId(), message.getOrderNo(), message.getProjectId(), message.getQuantity());

        try {
            // 调用Service层处理业务逻辑
            projectOrderService.cancelUnpaidOrder(
                    message.getOrderId(), 
                    message.getProjectId(), 
                    message.getQuantity()
            );

            log.info("[OrderCancelConsumer] 订单取消消息处理完成，订单ID: {}, 订单号: {}",
                    message.getOrderId(), message.getOrderNo());

        } catch (Exception e) {
            log.error("[OrderCancelConsumer] 订单取消消息处理失败，订单ID: {}, 订单号: {}, 错误信息: {}",
                    message.getOrderId(), message.getOrderNo(), e.getMessage(), e);
            // 抛出异常，让消息重新入队
            throw e;
        }
    }

}
