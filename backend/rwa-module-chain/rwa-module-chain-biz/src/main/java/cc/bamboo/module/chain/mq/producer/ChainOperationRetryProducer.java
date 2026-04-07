package cc.bamboo.module.chain.mq.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 链操作重试消息生产者
 * 用于发送延时重试消息
 *
 * @author Swolf
 */
@Component
@Slf4j
public class ChainOperationRetryProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 重试延时时间（分钟）：1, 2, 4, 8, 16
     */
    private static final long[] RETRY_DELAYS = {1, 2, 4, 8, 16};

    /**
     * 发送项目审核通过重试消息
     *
     * @param exchange 交换机名称
     * @param routingKey 路由键
     * @param message 消息对象
     * @param retryCount 当前重试次数
     */
    public void sendRetryMessage(String exchange, String routingKey, Object message, int retryCount) {
        try {
            // 计算延时时间
            long delayMinutes = getDelayMinutes(retryCount);
            long delayMillis = delayMinutes * 60 * 1000;

            log.info("[ChainOperationRetryProducer] 发送重试消息，exchange: {}, routingKey: {}, retryCount: {}, delayMinutes: {}",
                    exchange, routingKey, retryCount, delayMinutes);

            // 发送延时消息
            rabbitTemplate.convertAndSend(exchange, routingKey, message, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message msg) throws AmqpException {
                    // 设置消息延时时间（使用 x-delay 头）
                    msg.getMessageProperties().setDelay((int) delayMillis);
                    return msg;
                }
            });

            log.info("[ChainOperationRetryProducer] 重试消息发送成功，exchange: {}, routingKey: {}, retryCount: {}, delayMinutes: {}",
                    exchange, routingKey, retryCount, delayMinutes);

        } catch (Exception e) {
            log.error("[ChainOperationRetryProducer] 重试消息发送失败，exchange: {}, routingKey: {}, retryCount: {}, 错误: {}",
                    exchange, routingKey, retryCount, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据重试次数获取延时时间（分钟）
     * 指数退避：1, 2, 4, 8, 16 分钟
     *
     * @param retryCount 重试次数
     * @return 延时时间（分钟）
     */
    private long getDelayMinutes(int retryCount) {
        if (retryCount < 1 || retryCount > RETRY_DELAYS.length) {
            return RETRY_DELAYS[RETRY_DELAYS.length - 1];
        }
        return RETRY_DELAYS[retryCount - 1];
    }
}
