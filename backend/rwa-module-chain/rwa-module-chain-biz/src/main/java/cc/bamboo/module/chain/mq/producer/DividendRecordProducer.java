package cc.bamboo.module.chain.mq.producer;

import cc.bamboo.module.chain.mq.message.DividendRecordMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 分红记录消息生产者
 * 用于发送延时检查消息
 *
 * @author Swolf
 */
@Component
@Slf4j
public class DividendRecordProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 交换机名称
     */
    public static final String EXCHANGE_NAME = "chain.dividend.check.exchange";

    /**
     * 路由键
     */
    public static final String ROUTING_KEY = "chain.dividend.check";

    /**
     * 重试延时时间（秒）：30, 60, 120, 600, 3600
     */
    private static final long[] RETRY_DELAYS_SECONDS = { 30, 60, 120, 600, 3600 };

    /**
     * 发送延时检查消息
     *
     * @param message 消息对象
     */
    public void sendCheckMessage(DividendRecordMessage message) {
        int retryCount = message.getRetryCount() != null ? message.getRetryCount() : 0;
        long delaySeconds = getDelaySeconds(retryCount);
        long delayMillis = delaySeconds * 1000;

        log.info("[DividendRecordProducer] 发送检查消息，logId: {}, txHash: {}, retryCount: {}, delaySeconds: {}",
                message.getLogId(), message.getTransactionHash(), retryCount, delaySeconds);

        try {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message msg) throws AmqpException {
                    // 设置消息延时时间（使用 x-delay 头）
                    msg.getMessageProperties().setDelay((int) delayMillis);
                    return msg;
                }
            });
        } catch (Exception e) {
            log.error("[DividendRecordProducer] 发送检查消息失败，logId: {}, 错误: {}",
                    message.getLogId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据重试次数获取延时时间（秒）
     */
    public long getDelaySeconds(int retryCount) {
        if (retryCount <= 0) {
            return RETRY_DELAYS_SECONDS[0];
        }
        if (retryCount >= RETRY_DELAYS_SECONDS.length) {
            return RETRY_DELAYS_SECONDS[RETRY_DELAYS_SECONDS.length - 1];
        }
        return RETRY_DELAYS_SECONDS[retryCount];
    }

    /**
     * 获取最大重试次数
     */
    public int getMaxRetryCount() {
        return RETRY_DELAYS_SECONDS.length;
    }
}
