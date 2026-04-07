package cc.bamboo.module.chain.mq.producer;


import cc.bamboo.module.user.mq.message.UserClaimMessage;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 签发生产者
 * 发送消息通知 chain 模块添加用户并签发 Claim
 *
 * @author Swolf
 */
@Component
@Slf4j
public class ClaimProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "user.claim.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "user.claim";

    /**
     * 发送签发
     *
     * @param userId 签发
     */
    public void sendUserClaimMessage(Long userId) {

        // 生成任务编号
        String taskNo = generateTaskNo("CLAIM");

        log.info("[ChainOperationTaskService] 创建签发任务，taskNo: {}, 用户ID: {}",
                taskNo,  userId);

        try {
            // 构建消息
            UserClaimMessage message = new UserClaimMessage();
            message.setTaskNo(taskNo);
            message.setUserId(userId);
            message.setRetryCount(0);

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);

            log.info("[ClaimProducer] 签发发送成功，taskNo: {}, 用户ID: {}",
                    message.getTaskNo(),  message.getUserId());

        } catch (Exception e) {
            log.error("[ClaimProducer] 签发发送失败，taskNo: {}, 用户ID: {}, 错误信息: {}",
                    taskNo,  userId, e.getMessage(), e);
            // 抛出异常，让调用方知道消息发送失败
            throw new RuntimeException("签发发送失败: " + e.getMessage(), e);
        }
    }

    private String generateTaskNo(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + IdUtil.fastSimpleUUID().substring(0, 8);
    }

}
