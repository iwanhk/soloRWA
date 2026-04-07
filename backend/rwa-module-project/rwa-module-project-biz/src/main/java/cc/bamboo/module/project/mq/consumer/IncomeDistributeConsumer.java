package cc.bamboo.module.project.mq.consumer;

import cc.bamboo.module.project.mq.message.IncomeDistributeMessage;
import cc.bamboo.module.project.service.projectrevenue.ProjectRevenueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 收益发放消息消费者
 * 负责消费收益发放消息并调用Service层处理业务逻辑
 *
 * @author Swolf
 */
@Component
@Slf4j
@RabbitListener(bindings = @QueueBinding(value = @Queue(name = "project.income.distribute.queue", durable = "true"), exchange = @Exchange(name = "project.income.distribute.exchange", type = ExchangeTypes.DIRECT, durable = "true"), key = "project.income.distribute"),
                // 设置并发消费者数量,提高处理速度
                concurrency = "5-10")
public class IncomeDistributeConsumer {

        @Resource
        private ProjectRevenueService projectRevenueService;

        /**
         * 处理收益发放消息
         * Consumer只负责消费消息，具体业务逻辑由Service层实现
         *
         * @param message 收益发放消息
         */
        @RabbitHandler
        public void onMessage(IncomeDistributeMessage message) {
                log.info("[IncomeDistributeConsumer] 收到收益发放消息，订单ID: {}, 收益金额: {}",
                                message.getOrderId(), message.getOrderDailyIncome());

                try {
                        // 调用Service层处理业务逻辑
                        projectRevenueService.processOrderIncome(message);

                        log.info("[IncomeDistributeConsumer] 收益发放消息处理完成，订单ID: {}",
                                        message.getOrderId());

                } catch (Exception e) {
                        log.error("[IncomeDistributeConsumer] 收益发放消息处理失败，订单ID: {}, 错误信息: {}",
                                        message.getOrderId(), e.getMessage(), e);
                        // 抛出异常，让消息重新入队
                        throw e;
                }
        }

}
