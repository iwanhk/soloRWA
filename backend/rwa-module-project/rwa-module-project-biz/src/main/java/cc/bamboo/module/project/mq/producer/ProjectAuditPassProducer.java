package cc.bamboo.module.project.mq.producer;

import cc.bamboo.module.project.mq.message.ProjectAuditPassMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 项目审核通过消息生产者
 * 发送消息通知 chain 模块为项目部署 Token
 *
 * @author Swolf
 */
@Component
@Slf4j
public class ProjectAuditPassProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "project.audit.pass.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "project.audit.pass";

    /**
     * 发送项目审核通过消息
     *
     * @param taskNo 任务编号
     * @param projectId 项目ID
     * @param projectName 项目名称
     * @param projectSymbol 项目符号
     */
    public void sendProjectAuditPassMessage(String taskNo, Long projectId, String projectName, String projectSymbol) {
        try {
            log.info("[ProjectAuditPassProducer] 开始发送项目审核通过消息，taskNo: {}, 项目ID: {}, 项目名称: {}",
                    taskNo, projectId, projectName);

            ProjectAuditPassMessage message = new ProjectAuditPassMessage();
            message.setTaskNo(taskNo);
            message.setProjectId(projectId);
            message.setProjectName(projectName);
            message.setProjectSymbol(projectSymbol);
            message.setRetryCount(0);

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);

            log.info("[ProjectAuditPassProducer] 项目审核通过消息发送成功，taskNo: {}, 项目ID: {}, 项目名称: {}",
                    taskNo, projectId, projectName);

        } catch (Exception e) {
            log.error("[ProjectAuditPassProducer] 项目审核通过消息发送失败，taskNo: {}, 项目ID: {}, 项目名称: {}, 错误信息: {}",
                    taskNo, projectId, projectName, e.getMessage(), e);
            // 抛出异常，让调用方知道消息发送失败
            throw new RuntimeException("项目审核通过消息发送失败: " + e.getMessage(), e);
        }
    }

}
