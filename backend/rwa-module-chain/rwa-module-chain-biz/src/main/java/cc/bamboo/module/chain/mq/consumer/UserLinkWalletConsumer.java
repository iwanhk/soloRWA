package cc.bamboo.module.chain.mq.consumer;

import cc.bamboo.module.chain.dal.dataobject.chainoperationtask.ChainOperationTaskDO;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.module.chain.dal.mysql.claimissueridentities.ClaimIssuerIdentitiesMapper;
import cc.bamboo.module.chain.dal.mysql.claimtopics.ClaimTopicsMapper;
import cc.bamboo.module.chain.dal.mysql.tokens.TokensMapper;
import cc.bamboo.module.chain.enums.ChainTaskStatusEnum;
import cc.bamboo.module.chain.mq.producer.ChainOperationRetryProducer;
import cc.bamboo.module.chain.service.chainoperationtask.ChainOperationTaskHelper;
import cc.bamboo.module.chain.service.chainoperationtask.ChainOperationTaskService;
import cc.bamboo.module.chain.service.identity.IdentityService;
import cc.bamboo.module.chain.service.identity.dto.IdentityOperationRespDTO;
import cc.bamboo.module.chain.service.identity.dto.LinkNewWalletRespDTO;
import cc.bamboo.module.user.enums.ChainAddressStatusEnum;
import cc.bamboo.module.user.mq.message.UserCreateMessage;
import cc.bamboo.module.user.mq.message.UserLinkAddressMessage;
import cn.hutool.json.JSONUtil;
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
                        name = "user.link.queue",
                        durable = "true"
                ),
                exchange = @Exchange(
                        name = "user.link.exchange",
                        type = ExchangeTypes.DIRECT,
                        delayed = "true", // 启用延时交换机
                        durable = "true"
                ),
                key = "user.link"
        )
)
public class UserLinkWalletConsumer {

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
    private static final String EXCHANGE_NAME = "user.link.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "user.link";

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
    public void onMessage(UserLinkAddressMessage message) {
        String taskNo = message.getTaskNo();
        Integer retryCount = message.getRetryCount() != null ? message.getRetryCount() : 0;

        log.info("[UserLinkConsumer] 收到用户绑定钱包地址消息，taskNo: {}, 用户ID: {}, 重试次数: {}",
                taskNo,  message.getUserId(), retryCount);
        //查询project = 0 的token（签发专用）
        TokensDO tokensDO = tokensMapper.getSignToken();
        // 1. 创建或获取任务
        ChainOperationTaskDO task = chainOperationTaskHelper.createOrGetAddUserTask(
                taskNo, null, null, null,
                null, tokensDO.getAddress(), message.getUserId(),
                message.getUserAddress(), null);
        if (task == null) {
            log.error("[UserLinkConsumer] 任务创建失败，taskNo: {}", taskNo);
            return;
        }

        // 2. 检查任务状态（幂等性）
        if (ChainTaskStatusEnum.SUCCESS.getStatus().equals(task.getStatus())) {
            log.info("[UserLinkConsumer] 任务已成功，跳过执行，taskNo: {}", taskNo);
            return;
        }

        // 3. 更新任务状态为处理中
        chainOperationTaskService.updateTaskToProcessing(taskNo);

        List<String> executionSteps = new ArrayList<>();
        try {
            executionSteps.add("开始用户地址");
            // 4. 添加用户到白名单
            // 注意：这里需要传入 tokenId，需要根据 tokenAddress 查询 tokenId
            // 暂时传 null，让 addUser 方法内部处理
            LinkNewWalletRespDTO linkWallet = identityService.linkWallet(
                    message.getUserAddress(),
                    message.getUserId()
            );
            executionSteps.add("添加用户地址成功");


            // 6. 更新任务状态为成功
            chainOperationTaskService.updateTaskToSuccess(taskNo, JSONUtil.toJsonStr(executionSteps),linkWallet.getTransactionHash());

            // 7.更新该地址为绑定成功状态
            tokensMapper.updateUserChainAddress(ChainAddressStatusEnum.SUCCESS.getStatus(),  message.getUserChainId());

            log.info("[UserLinkConsumer] 用户添加地址，taskNo: {},  用户地址: {}",
                    taskNo,  message.getUserAddress());

        } catch (Exception e) {
            executionSteps.add("执行失败: " + e.getMessage());
            log.error("[UserLinkConsumer] 用户添加地址，taskNo: {},  错误: {}",
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

                log.info("[UserLinkConsumer] 已发送重试消息，taskNo: {}, 重试次数: {}/{}", 
                        taskNo, nextRetryCount, MAX_RETRY_COUNT);
            } else {
                // 达到最大重试次数，标记为最终失败
                chainOperationTaskService.updateTaskToFailed(taskNo, nextRetryCount, 
                        "达到最大重试次数: " + e.getMessage(), JSONUtil.toJsonStr(executionSteps));
                tokensMapper.updateUserChainAddress(ChainAddressStatusEnum.BIND_FAIL.getStatus(),  message.getUserChainId());
                log.error("[UserLinkConsumer] 任务最终失败，taskNo: {}, 已重试 {} 次", taskNo, MAX_RETRY_COUNT);
            }
        }
    }

}
