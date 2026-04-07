package cc.bamboo.module.chain.mq.consumer;

import cc.bamboo.module.chain.dal.dataobject.chainoperationtask.ChainOperationTaskDO;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.module.chain.dal.mysql.tokens.TokensMapper;
import cc.bamboo.module.chain.enums.ChainTaskStatusEnum;
import cc.bamboo.module.project.mq.message.OrderAuditPassMessage;
import cc.bamboo.module.chain.mq.producer.ChainOperationRetryProducer;
import cc.bamboo.module.chain.service.chainoperationtask.ChainOperationTaskHelper;
import cc.bamboo.module.chain.service.chainoperationtask.ChainOperationTaskService;
import cc.bamboo.module.chain.service.token.TokenMintService;
import cc.bamboo.module.chain.service.token.dto.MintTokenReqDTO;
import cc.bamboo.module.chain.service.token.dto.MintTokenRespDTO;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单审核通过消息消费者
 * 负责接收订单审核通过消息，为用户铸造 Token
 *
 * @author Swolf
 */
@Component
@Slf4j
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        name = "order.audit.pass.queue",
                        durable = "true"
                ),
                exchange = @Exchange(
                        name = "order.audit.pass.exchange",
                        type = ExchangeTypes.DIRECT,
                        delayed = "true", // 启用延时交换机
                        durable = "true"
                ),
                key = "order.audit.pass"
        )
)
public class OrderAuditPassConsumer {

    @Resource
    private TokenMintService tokenMintService;

    @Resource
    private TokensMapper tokensMapper;

    @Resource
    private ChainOperationTaskService chainOperationTaskService;

    @Resource
    private ChainOperationRetryProducer chainOperationRetryProducer;

    @Resource
    private ChainOperationTaskHelper chainOperationTaskHelper;

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "order.audit.pass.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "order.audit.pass";

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 5;

    /**
     * 处理订单审核通过消息
     * 为用户铸造 Token
     *
     * @param message 订单审核通过消息
     */
    @RabbitHandler
    public void onMessage(OrderAuditPassMessage message) {
        String taskNo = message.getTaskNo();
        Integer retryCount = message.getRetryCount() != null ? message.getRetryCount() : 0;

        log.info("[OrderAuditPassConsumer] 收到订单审核通过消息，taskNo: {}, 订单ID: {}, 用户ID: {}, 铸造数量: {}, 重试次数: {}",
                taskNo, message.getOrderId(), message.getUserId(), message.getMintAmount(), retryCount);

        // 1. 创建或获取任务
        ChainOperationTaskDO task = chainOperationTaskHelper.createOrGetMintTokenTask(
                taskNo, message.getOrderId(), message.getOrderNo(), message.getProjectId(),
                message.getProjectName(), message.getTokenAddress(), message.getUserId(),
                message.getUserAddress(), message.getMintAmount());
        if (task == null) {
            log.error("[OrderAuditPassConsumer] 任务创建失败，taskNo: {}", taskNo);
            return;
        }

        // 2. 检查任务状态（幂等性）
        if (ChainTaskStatusEnum.SUCCESS.getStatus().equals(task.getStatus())) {
            log.info("[OrderAuditPassConsumer] 任务已成功，跳过执行，taskNo: {}", taskNo);
            return;
        }

        // 3. 更新任务状态为处理中
        chainOperationTaskService.updateTaskToProcessing(taskNo);

        List<String> executionSteps = new ArrayList<>();
        try {
            executionSteps.add("开始铸造 Token");

            // 4. 根据 Token 地址查询 Token 信息
            TokensDO token = tokensMapper.selectOne(TokensDO::getAddress, message.getTokenAddress());
            if (token == null) {
                throw new RuntimeException("未找到 Token，地址: " + message.getTokenAddress());
            }
            executionSteps.add("查询到 Token，ID: " + token.getId() + ", 名称: " + token.getName());

            // 5. 构建铸造请求
            MintTokenReqDTO mintReq = new MintTokenReqDTO();
            mintReq.setTokenId(token.getId());
            mintReq.setToAddress(message.getUserAddress());
            mintReq.setAmount(message.getMintAmount());

            // 6. 调用铸造服务
            MintTokenRespDTO mintResp = tokenMintService.mint(mintReq);
            executionSteps.add("Token 铸造成功，TxHash: " + mintResp.getTransactionHash() + 
                    ", BlockNumber: " + mintResp.getBlockNumber());

            // 7. 更新任务状态为成功
            chainOperationTaskService.updateTaskToSuccess(taskNo, JSONUtil.toJsonStr(executionSteps),mintResp.getTransactionHash());
            // 8. 更新订单状态为已铸造
            tokensMapper.updateOrder(message.getOrderId(), ChainTaskStatusEnum.SUCCESS.getStatus(),
                    mintResp.getTransactionHash());

            log.info("[OrderAuditPassConsumer] Token 铸造成功，taskNo: {}, 订单ID: {}, 用户地址: {}, 数量: {}, TxHash: {}",
                    taskNo, message.getOrderId(), message.getUserAddress(), message.getMintAmount(), 
                    mintResp.getTransactionHash());

        } catch (Exception e) {
            executionSteps.add("执行失败: " + e.getMessage());
            log.error("[OrderAuditPassConsumer] Token 铸造失败，taskNo: {}, 订单ID: {}, 错误: {}",
                    taskNo, message.getOrderId(), e.getMessage(), e);

            // 8. 判断是否需要重试
            int nextRetryCount = retryCount + 1;
            if (nextRetryCount <= MAX_RETRY_COUNT) {
                // 更新任务状态为失败（但不是最终失败）
                chainOperationTaskService.updateTaskToFailed(taskNo, nextRetryCount, e.getMessage(), 
                        JSONUtil.toJsonStr(executionSteps));

                // 发送延时重试消息
                message.setRetryCount(nextRetryCount);
                chainOperationRetryProducer.sendRetryMessage(EXCHANGE_NAME, ROUTING_KEY, message, nextRetryCount);

                log.info("[OrderAuditPassConsumer] 已发送重试消息，taskNo: {}, 重试次数: {}/{}", 
                        taskNo, nextRetryCount, MAX_RETRY_COUNT);
            } else {
                // 达到最大重试次数，标记为最终失败
                chainOperationTaskService.updateTaskToFailed(taskNo, nextRetryCount, 
                        "达到最大重试次数: " + e.getMessage(), JSONUtil.toJsonStr(executionSteps));
                // 更新订单状态为铸造失败
                tokensMapper.updateOrder(message.getOrderId(), ChainTaskStatusEnum.FAILED.getStatus(),
                        null);

                log.error("[OrderAuditPassConsumer] 任务最终失败，taskNo: {}, 已重试 {} 次", taskNo, MAX_RETRY_COUNT);
            }
        }
    }

}
