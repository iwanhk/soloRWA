package cc.bamboo.module.chain.mq.consumer;

import cc.bamboo.module.chain.dal.dataobject.chainoperationtask.ChainOperationTaskDO;
import cc.bamboo.module.chain.dal.dataobject.claimissueridentities.ClaimIssuerIdentitiesDO;
import cc.bamboo.module.chain.dal.dataobject.claimtopics.ClaimTopicsDO;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.module.chain.dal.dataobject.useridentities.UserIdentitiesDO;
import cc.bamboo.module.chain.dal.mysql.claimissueridentities.ClaimIssuerIdentitiesMapper;
import cc.bamboo.module.chain.dal.mysql.claimtopics.ClaimTopicsMapper;
import cc.bamboo.module.chain.dal.mysql.tokens.TokensMapper;
import cc.bamboo.module.chain.dal.mysql.useridentities.UserIdentitiesMapper;
import cc.bamboo.module.chain.enums.ChainTaskStatusEnum;
import cc.bamboo.module.chain.mq.producer.ChainOperationRetryProducer;
import cc.bamboo.module.chain.service.chainoperationtask.ChainOperationTaskHelper;
import cc.bamboo.module.chain.service.chainoperationtask.ChainOperationTaskService;
import cc.bamboo.module.chain.service.identity.IdentityService;
import cc.bamboo.module.chain.service.identity.dto.IdentityOperationRespDTO;
import cc.bamboo.module.chain.service.identity.dto.IssueClaimReqDTO;
import cc.bamboo.module.chain.service.identity.dto.IssueClaimRespDTO;
import cc.bamboo.module.project.mq.message.OrderCreateMessage;
import cc.bamboo.module.user.mq.message.UserClaimMessage;
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
 * 用户签发消息消费者
 * 负责接收用户签发消息，添加用户到白名单并签发 Claim
 *
 * @author Swolf
 */
@Component
@Slf4j
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        name = "user.claim.queue",
                        durable = "true"
                ),
                exchange = @Exchange(
                        name = "user.claim.exchange",
                        type = ExchangeTypes.DIRECT,
                        delayed = "true", // 启用延时交换机
                        durable = "true"
                ),
                key = "user.claim"
        )
)
public class ClaimConsumer {

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
    private UserIdentitiesMapper userIdentitiesMapper;

    @Resource
    private ClaimIssuerIdentitiesMapper claimIssuerIdentitiesMapper;

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "user.claim.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "user.claim";

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 5;

    /**
     * 处理用户签发消息
     * 添加用户到白名单并签发 Claim
     *
     * @param message 用户签发消息
     */
    @RabbitHandler
    public void onMessage(UserClaimMessage message) {
        String taskNo = message.getTaskNo();
        Integer retryCount = message.getRetryCount() != null ? message.getRetryCount() : 0;

        log.info("[UserClaimConsumer] 收到用户签发消息，taskNo: {},  用户ID: {}, 重试次数: {}",
                taskNo,  message.getUserId(), retryCount);

        // 1. 创建或获取任务
        ChainOperationTaskDO task = chainOperationTaskHelper.createOrGetAddUserTask(
                taskNo, null, null, null,
                null, null, message.getUserId(),
                null, null);
        if (task == null) {
            log.error("[UserClaimConsumer] 任务创建失败，taskNo: {}", taskNo);
            return;
        }

        // 2. 检查任务状态（幂等性）
        if (ChainTaskStatusEnum.SUCCESS.getStatus().equals(task.getStatus())) {
            log.info("[UserClaimConsumer] 任务已成功，跳过执行，taskNo: {}", taskNo);
            return;
        }

        // 3. 更新任务状态为处理中
        chainOperationTaskService.updateTaskToProcessing(taskNo);

        List<String> executionSteps = new ArrayList<>();
        try {
            executionSteps.add("开始签发 Claim");
            LambdaQueryWrapper<UserIdentitiesDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserIdentitiesDO::getUserId, message.getUserId());
            lambdaQueryWrapper.last("limit 1");
            UserIdentitiesDO userIdentitiesDO = userIdentitiesMapper.selectOne(lambdaQueryWrapper);
            List<Long> topicIds = parseLongList(userIdentitiesDO.getTopicIds());
            List<ClaimTopicsDO> topicsDOList = claimTopicsMapper.selectList();
            ClaimIssuerIdentitiesDO claimIssuerIdentitiesDO = claimIssuerIdentitiesMapper.selectOne(new LambdaQueryWrapper<ClaimIssuerIdentitiesDO>()
                    .last("limit 1"));
            if(!topicsDOList.isEmpty()){
                for (ClaimTopicsDO topicsDO : topicsDOList) {
                    if(topicIds != null && !topicIds.contains(topicsDO.getId())){
                        // 5. 签发 Claim
                        IssueClaimReqDTO issueClaimReq = new IssueClaimReqDTO();
                        issueClaimReq.setUserId(userIdentitiesDO.getId());

                        // 其他参数根据实际需求设置
                        issueClaimReq.setClaimIssuerId(claimIssuerIdentitiesDO.getId());
                        issueClaimReq.setTopic(topicsDO.getTopic());
                        issueClaimReq.setData("0x");
                        issueClaimReq.setUri("0x");
                        issueClaimReq.setScheme(1);
                        IssueClaimRespDTO issueClaimResp = identityService.issueClaim(issueClaimReq);
                        executionSteps.add("签发 Claim 成功，TxHash: " + issueClaimResp.getTransactionHash());
                        //更新用户 Identity 的topicIds
                        identityService.updateUserIdentityTopicIds(userIdentitiesDO.getId(), topicsDO.getId());
                    }
                }
            }



            // 6. 更新任务状态为成功
            chainOperationTaskService.updateTaskToSuccess(taskNo, JSONUtil.toJsonStr(executionSteps),null);

            log.info("[UserClaimConsumer] 用户添加和 Claim 签发成功，taskNo: {},  用户id: {}",
                    taskNo,  message.getUserId());

        } catch (Exception e) {
            executionSteps.add("执行失败: " + e.getMessage());
            log.error("[UserClaimConsumer] 用户添加和 Claim 签发失败，taskNo: {},  错误: {}",
                    taskNo,  e.getMessage(), e);

            // 7. 判断是否需要重试
            int nextRetryCount = retryCount + 1;
            if (nextRetryCount <= MAX_RETRY_COUNT) {
                // 更新任务状态为失败（但不是最终失败）
                chainOperationTaskService.updateTaskToFailed(taskNo, nextRetryCount, e.getMessage(), 
                        JSONUtil.toJsonStr(executionSteps));

                // 发送延时重试消息
                message.setRetryCount(nextRetryCount);
                chainOperationRetryProducer.sendRetryMessage(EXCHANGE_NAME, ROUTING_KEY, message, nextRetryCount);

                log.info("[UserClaimConsumer] 已发送重试消息，taskNo: {}, 重试次数: {}/{}", 
                        taskNo, nextRetryCount, MAX_RETRY_COUNT);
            } else {
                // 达到最大重试次数，标记为最终失败
                chainOperationTaskService.updateTaskToFailed(taskNo, nextRetryCount, 
                        "达到最大重试次数: " + e.getMessage(), JSONUtil.toJsonStr(executionSteps));

                log.error("[UserClaimConsumer] 任务最终失败，taskNo: {}, 已重试 {} 次", taskNo, MAX_RETRY_COUNT);
            }
        }
    }

    private List<Long> parseLongList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return JSONUtil.toList(json, Long.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}
