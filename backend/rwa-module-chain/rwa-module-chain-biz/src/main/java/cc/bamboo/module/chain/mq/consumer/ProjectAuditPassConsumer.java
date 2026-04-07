package cc.bamboo.module.chain.mq.consumer;

import cc.bamboo.module.chain.dal.dataobject.blockchainaddresses.BlockchainAddressesDO;
import cc.bamboo.module.chain.dal.dataobject.chainoperationtask.ChainOperationTaskDO;
import cc.bamboo.module.chain.dal.dataobject.claimissueridentities.ClaimIssuerIdentitiesDO;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.module.chain.dal.mysql.blockchainaddresses.BlockchainAddressesMapper;
import cc.bamboo.module.chain.dal.mysql.claimissueridentities.ClaimIssuerIdentitiesMapper;
import cc.bamboo.module.chain.dal.mysql.tokens.TokensMapper;
import cc.bamboo.module.chain.enums.ChainTaskStatusEnum;
import cc.bamboo.module.project.mq.message.ProjectAuditPassMessage;
import cc.bamboo.module.chain.mq.producer.ChainOperationRetryProducer;
import cc.bamboo.module.chain.service.chainoperationtask.ChainOperationTaskHelper;
import cc.bamboo.module.chain.service.chainoperationtask.ChainOperationTaskService;
import cc.bamboo.module.chain.service.trex.TrexDeployService;
import cc.bamboo.module.chain.service.trex.dto.DeployTokenReqDTO;
import cc.bamboo.module.project.api.projectinfo.ProjectInfoApi;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 项目审核通过消息消费者
 * 负责接收项目审核通过消息并为项目部署 Token
 *
 * @author Swolf
 */
@Component
@Slf4j
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        name = "project.audit.pass.queue",
                        durable = "true"
                ),
                exchange = @Exchange(
                        name = "project.audit.pass.exchange",
                        type = ExchangeTypes.DIRECT,
                        delayed = "true", // 启用延时交换机
                        durable = "true"
                ),
                key = "project.audit.pass"
        )
)
public class ProjectAuditPassConsumer {

    @Resource
    private TrexDeployService trexDeployService;

    @Resource
    private BlockchainAddressesMapper blockchainAddressesMapper;

    @Resource
    private ClaimIssuerIdentitiesMapper claimIssuerIdentitiesMapper;

    @Resource
    private ChainOperationTaskService chainOperationTaskService;

    @Resource
    private ChainOperationRetryProducer chainOperationRetryProducer;

    @Resource
    private ChainOperationTaskHelper chainOperationTaskHelper;

    @Resource
    private ProjectInfoApi projectInfoApi;

    @Resource
    private TokensMapper tokensMapper;

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "project.audit.pass.exchange";

    /**
     * 路由键
     */
    private static final String ROUTING_KEY = "project.audit.pass";

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 5;

    /**
     * 处理项目审核通过消息
     * 为项目部署 Token
     *
     * @param message 项目审核通过消息
     */
    @RabbitHandler
    public void onMessage(ProjectAuditPassMessage message) {
        String taskNo = message.getTaskNo();
        Integer retryCount = message.getRetryCount() != null ? message.getRetryCount() : 0;

        log.info("[ProjectAuditPassConsumer] 收到项目审核通过消息，taskNo: {}, 项目ID: {}, 项目名称: {}, 重试次数: {}",
                taskNo, message.getProjectId(), message.getProjectName(), retryCount);

        // 1. 创建或获取任务
        ChainOperationTaskDO task = chainOperationTaskHelper.createOrGetDeployTokenTask(
                taskNo, message.getProjectId(), message.getProjectName(), message.getProjectSymbol());
        if (task == null) {
            log.error("[ProjectAuditPassConsumer] 任务创建失败，taskNo: {}", taskNo);
            return;
        }

        // 2. 检查任务状态（幂等性）
        if (ChainTaskStatusEnum.SUCCESS.getStatus().equals(task.getStatus())) {
            log.info("[ProjectAuditPassConsumer] 任务已成功，跳过执行，taskNo: {}", taskNo);
            return;
        }
        //查看该项目是否已部署
        TokensDO tokensDO = tokensMapper.selectOne(TokensDO::getProjectId, message.getProjectId());
        if (tokensDO != null) {
            log.error("[ProjectAuditPassConsumer] 项目已部署 Token，跳过执行，taskNo: {}", taskNo);
            return;
        }

        // 3. 更新任务状态为处理中
        chainOperationTaskService.updateTaskToProcessing(taskNo);

        List<String> executionSteps = new ArrayList<>();
        try {
            executionSteps.add("开始部署 Token");

            // 4. 获取 deployer 地址作为所有者地址
            BlockchainAddressesDO deployer = blockchainAddressesMapper.selectOne(
                    BlockchainAddressesDO::getName, "deployer");
            if (deployer == null) {
                throw new RuntimeException("未找到 deployer 地址");
            }
            executionSteps.add("获取 deployer 地址: " + deployer.getAddress());

            // 5. 获取所有 ClaimIssuer Identity 合约地址
            List<ClaimIssuerIdentitiesDO> claimIssuers = claimIssuerIdentitiesMapper.selectList();
            if (claimIssuers == null || claimIssuers.isEmpty()) {
                throw new RuntimeException("未找到 ClaimIssuer Identity");
            }

            List<String> issuerAddresses = claimIssuers.stream()
                    .map(ClaimIssuerIdentitiesDO::getContractAddress)
                    .collect(Collectors.toList());
            executionSteps.add("获取 ClaimIssuer 地址数量: " + issuerAddresses.size());

            // 6. 构建部署请求
            DeployTokenReqDTO reqDTO = new DeployTokenReqDTO();
            reqDTO.setSalt(UUID.randomUUID().toString());
            reqDTO.setOwnerAddress(deployer.getAddress());
            reqDTO.setName(message.getProjectName());
            reqDTO.setSymbol(message.getProjectSymbol());
            reqDTO.setDecimals(18);
            reqDTO.setTokenAgents(Collections.singletonList(deployer.getAddress()));
            reqDTO.setIssuers(issuerAddresses);
            reqDTO.setProjectId(message.getProjectId());
            executionSteps.add("构建部署请求完成");

            // 7. 调用部署服务
            TokensDO token = trexDeployService.deployToken(reqDTO);
            executionSteps.add("Token 部署成功，地址: " + token.getAddress());

            // 8. 更新任务的 Token 地址
            chainOperationTaskService.updateTokenAddress(taskNo, token.getAddress());

            // 9. 回写项目链上信息
            int result = tokensMapper.updateChainInfo(
                    message.getProjectId(),
                    ChainTaskStatusEnum.SUCCESS.getStatus(),
                    token.getId(),
                    token.getAddress()
            );
            if (result != 1) {
                //重试一次
                result = tokensMapper.updateChainInfo(
                        message.getProjectId(),
                        ChainTaskStatusEnum.SUCCESS.getStatus(),
                        token.getId(),
                        token.getAddress()
                );
                if (result != 1) {
                    //记录日志
                    log.error("[ProjectAuditPassConsumer] 回写项目链上信息失败，projectId: {}, tokenId: {}, tokenAddress: {}",
                            message.getProjectId(), token.getId(), token.getAddress());
                }
            }

            // 10. 更新任务状态为成功
            chainOperationTaskService.updateTaskToSuccess(taskNo, JSONUtil.toJsonStr(executionSteps),token.getTransactionHash());

            log.info("[ProjectAuditPassConsumer] Token 部署成功，taskNo: {}, 项目ID: {}, Token地址: {}",
                    taskNo, message.getProjectId(), token.getAddress());

        } catch (Exception e) {
            executionSteps.add("执行失败: " + e.getMessage());
            log.error("[ProjectAuditPassConsumer] Token 部署失败，taskNo: {}, 项目ID: {}, 错误: {}",
                    taskNo, message.getProjectId(), e.getMessage(), e);

            // 10. 判断是否需要重试
            int nextRetryCount = retryCount + 1;
            if (nextRetryCount <= MAX_RETRY_COUNT) {
                // 更新任务状态为失败（但不是最终失败）
                chainOperationTaskService.updateTaskToFailed(taskNo, nextRetryCount, e.getMessage(), 
                        JSONUtil.toJsonStr(executionSteps));

                // 发送延时重试消息
                message.setRetryCount(nextRetryCount);
                chainOperationRetryProducer.sendRetryMessage(EXCHANGE_NAME, ROUTING_KEY, message, nextRetryCount);

                log.info("[ProjectAuditPassConsumer] 已发送重试消息，taskNo: {}, 重试次数: {}/{}", 
                        taskNo, nextRetryCount, MAX_RETRY_COUNT);
            } else {
                // 达到最大重试次数，标记为最终失败
                chainOperationTaskService.updateTaskToFailed(taskNo, nextRetryCount, 
                        "达到最大重试次数: " + e.getMessage(), JSONUtil.toJsonStr(executionSteps));

                log.error("[ProjectAuditPassConsumer] 任务最终失败，taskNo: {}, 已重试 {} 次", taskNo, MAX_RETRY_COUNT);
                //更新项目链上状态为失败
                tokensMapper.updateChainInfo(
                        message.getProjectId(),
                        ChainTaskStatusEnum.FAILED.getStatus(),
                        null,
                        null
                );
            }
        }
    }

}
