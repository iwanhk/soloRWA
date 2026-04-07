package cc.bamboo.module.chain.mq.consumer;

import cc.bamboo.module.chain.dal.dataobject.chainoperationtask.ChainOperationTaskDO;
import cc.bamboo.module.chain.dal.dataobject.claimissueridentities.ClaimIssuerIdentitiesDO;
import cc.bamboo.module.chain.dal.dataobject.claimtopics.ClaimTopicsDO;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.module.chain.dal.mysql.claimissueridentities.ClaimIssuerIdentitiesMapper;
import cc.bamboo.module.chain.dal.mysql.claimtopics.ClaimTopicsMapper;
import cc.bamboo.module.chain.dal.mysql.tokens.TokensMapper;
import cc.bamboo.module.chain.enums.ChainTaskStatusEnum;
import cc.bamboo.module.chain.service.claimtopics.ClaimTopicsService;
import cc.bamboo.module.project.mq.message.OrderCreateMessage;
import cc.bamboo.module.chain.mq.producer.ChainOperationRetryProducer;
import cc.bamboo.module.chain.service.chainoperationtask.ChainOperationTaskHelper;
import cc.bamboo.module.chain.service.chainoperationtask.ChainOperationTaskService;
import cc.bamboo.module.chain.service.identity.IdentityService;
import cc.bamboo.module.chain.service.identity.dto.IdentityOperationRespDTO;
import cc.bamboo.module.chain.service.identity.dto.IssueClaimReqDTO;
import cc.bamboo.module.chain.service.identity.dto.IssueClaimRespDTO;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户下单消息消费者
 * 负责接收用户下单消息，添加用户到白名单并签发 Claim
 *
 * @author Swolf
 */
@Component
@Slf4j
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        name = "order.create.queue",
                        durable = "true"
                ),
                exchange = @Exchange(
                        name = "order.create.exchange",
                        type = ExchangeTypes.DIRECT,
                        delayed = "true", // 启用延时交换机
                        durable = "true"
                ),
                key = "order.create"
        )
)
public class OrderCreateConsumer {

    @Resource
    private IdentityService identityService;

    @Resource
    private ChainOperationTaskService chainOperationTaskService;

    @Resource
    private ChainOperationRetryProducer chainOperationRetryProducer;

    @Resource
    private ChainOperationTaskHelper chainOperationTaskHelper;

    @Resource
    private ClaimTopicsMapper claimTopicsMapper;

    @Resource
    private TokensMapper tokensMapper;

    @Resource
    private ClaimIssuerIdentitiesMapper claimIssuerIdentitiesMapper;

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "order.create.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "order.create";

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 5;

    /**
     * 处理用户下单消息
     * 添加用户到白名单并签发 Claim
     *
     * @param message 用户下单消息
     */
    @RabbitHandler
    public void onMessage(OrderCreateMessage message) {

        String taskNo = message.getTaskNo();
        Integer retryCount = message.getRetryCount() != null ? message.getRetryCount() : 0;
        List<String> executionSteps = new ArrayList<>();
        try {
        log.info("[OrderCreateConsumer] 收到用户下单消息，taskNo: {}, 订单ID: {}, 用户ID: {}, 重试次数: {}",
                taskNo, message.getOrderId(), message.getUserId(), retryCount);

        // 1. 创建或获取任务
        ChainOperationTaskDO task = chainOperationTaskHelper.createOrGetAddUserTask(
                taskNo, message.getOrderId(), message.getOrderNo(), message.getProjectId(),
                message.getProjectName(), message.getTokenAddress(), message.getUserId(),
                message.getUserAddress(), message.getCountryCode());
        if (task == null) {
            log.error("[OrderCreateConsumer] 任务创建失败，taskNo: {}", taskNo);
            return;
        }

        // 2. 检查任务状态（幂等性）
        if (ChainTaskStatusEnum.SUCCESS.getStatus().equals(task.getStatus())) {
            log.info("[OrderCreateConsumer] 任务已成功，跳过执行，taskNo: {}", taskNo);
            return;
        }

        // 3. 更新任务状态为处理中
        chainOperationTaskService.updateTaskToProcessing(taskNo);


            executionSteps.add("开始添加用户并签发 Claim");
            TokensDO tokensDO = tokensMapper.selectOne(new LambdaQueryWrapper<TokensDO>()
                    .eq(TokensDO::getProjectId, message.getProjectId()));
            // 4. 添加用户到白名单
            // 注意：这里需要传入 tokenId，需要根据 tokenAddress 查询 tokenId
            // 暂时传 null，让 addUser 方法内部处理
            IdentityOperationRespDTO addUserResp = identityService.addUser(
                    message.getUserId(),
                    message.getUserAddress(),
                    tokensDO.getId(), // tokenId 需要根据实际情况传入
                    message.getCountryCode()
            );
            executionSteps.add("添加用户成功，Identity地址: " + addUserResp.getContractAddress());

            List<ClaimTopicsDO> topicsDOList = claimTopicsMapper.selectList();
            ClaimIssuerIdentitiesDO claimIssuerIdentitiesDO = claimIssuerIdentitiesMapper.selectOne(new LambdaQueryWrapper<ClaimIssuerIdentitiesDO>()
                    .last("limit 1"));
            if(!topicsDOList.isEmpty()){
                for (ClaimTopicsDO topicsDO : topicsDOList) {
                    if(addUserResp.getTopicIds() != null && !addUserResp.getTopicIds().contains(topicsDO.getId())){
                        // 5. 签发 Claim
                        IssueClaimReqDTO issueClaimReq = new IssueClaimReqDTO();
                        issueClaimReq.setUserId(addUserResp.getId());

                        // 其他参数根据实际需求设置
                        issueClaimReq.setClaimIssuerId(claimIssuerIdentitiesDO.getId());
                        issueClaimReq.setTopic(topicsDO.getTopic());
                        issueClaimReq.setData("0x");
                        issueClaimReq.setUri("0x");
                        issueClaimReq.setScheme(1);
                        IssueClaimRespDTO issueClaimResp = identityService.issueClaim(issueClaimReq);
                        executionSteps.add("签发 Claim 成功，TxHash: " + issueClaimResp.getTransactionHash());
                        //更新用户 Identity 的topicIds
                        identityService.updateUserIdentityTopicIds(addUserResp.getId(), topicsDO.getId());
                    }
                }
            }



            // 6. 更新任务状态为成功
            chainOperationTaskService.updateTaskToSuccess(taskNo, JSONUtil.toJsonStr(executionSteps),null);

            log.info("[OrderCreateConsumer] 用户添加和 Claim 签发成功，taskNo: {}, 订单ID: {}, 用户地址: {}",
                    taskNo, message.getOrderId(), message.getUserAddress());

        } catch (Exception e) {
            executionSteps.add("执行失败: " + e.getMessage());
            log.error("[OrderCreateConsumer] 用户添加和 Claim 签发失败，taskNo: {}, 订单ID: {}, 错误: {}",
                    taskNo, message.getOrderId(), e.getMessage(), e);

            // 7. 判断是否需要重试
            int nextRetryCount = retryCount + 1;
            if (nextRetryCount <= MAX_RETRY_COUNT) {
                // 更新任务状态为失败（但不是最终失败）
                chainOperationTaskService.updateTaskToFailed(taskNo, nextRetryCount, e.getMessage(), 
                        JSONUtil.toJsonStr(executionSteps));

                // 发送延时重试消息
                message.setRetryCount(nextRetryCount);
                chainOperationRetryProducer.sendRetryMessage(EXCHANGE_NAME, ROUTING_KEY, message, nextRetryCount);

                log.info("[OrderCreateConsumer] 已发送重试消息，taskNo: {}, 重试次数: {}/{}", 
                        taskNo, nextRetryCount, MAX_RETRY_COUNT);
            } else {
                // 达到最大重试次数，标记为最终失败
                chainOperationTaskService.updateTaskToFailed(taskNo, nextRetryCount, 
                        "达到最大重试次数: " + e.getMessage(), JSONUtil.toJsonStr(executionSteps));

                log.error("[OrderCreateConsumer] 任务最终失败，taskNo: {}, 已重试 {} 次", taskNo, MAX_RETRY_COUNT);
            }
        }
    }

}
