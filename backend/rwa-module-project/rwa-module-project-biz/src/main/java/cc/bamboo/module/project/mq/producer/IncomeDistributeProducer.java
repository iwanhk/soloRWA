package cc.bamboo.module.project.mq.producer;

import cc.bamboo.module.project.mq.message.IncomeDistributeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 收益发放消息生产者
 * 发送消息用于异步发放订单收益
 *
 * @author Swolf
 */
@Component
@Slf4j
public class IncomeDistributeProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "project.income.distribute.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "project.income.distribute";

    /**
     * 发送收益发放消息
     *
     * @param message 收益发放消息
     */
    public void sendIncomeDistributeMessage(IncomeDistributeMessage message) {
        try {
            log.debug("[IncomeDistributeProducer] 发送收益发放消息，订单ID: {}, 收益金额: {}",
                    message.getOrderId(), message.getOrderDailyIncome());

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);

        } catch (Exception e) {
            log.error("[IncomeDistributeProducer] 收益发放消息发送失败，订单ID: {}, 错误信息: {}",
                    message.getOrderId(), e.getMessage(), e);
            // 抛出异常，让调用方知道发送失败
            throw e;
        }
    }

}
