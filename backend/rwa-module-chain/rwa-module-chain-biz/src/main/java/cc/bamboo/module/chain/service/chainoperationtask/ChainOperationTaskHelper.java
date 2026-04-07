package cc.bamboo.module.chain.service.chainoperationtask;

import cc.bamboo.module.chain.dal.dataobject.chainoperationtask.ChainOperationTaskDO;
import cc.bamboo.module.chain.enums.ChainOperationTypeEnum;
import cc.bamboo.module.chain.enums.ChainTaskStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 链操作任务辅助类
 * 用于创建和初始化任务
 *
 * @author Swolf
 */
@Component
@Slf4j
public class ChainOperationTaskHelper {

    @Resource
    private ChainOperationTaskService chainOperationTaskService;

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 5;

    /**
     * 创建或获取部署 Token 任务
     *
     * @param taskNo 任务编号
     * @param projectId 项目ID
     * @param projectName 项目名称
     * @param projectSymbol 项目符号
     * @return 任务信息
     */
    public ChainOperationTaskDO createOrGetDeployTokenTask(String taskNo, Long projectId, 
                                                           String projectName, String projectSymbol) {
        // 先查询任务是否已存在
        ChainOperationTaskDO existTask = chainOperationTaskService.getByTaskNo(taskNo);
        if (existTask != null) {
            return existTask;
        }

        // 创建新任务
        ChainOperationTaskDO task = ChainOperationTaskDO.builder()
                .taskNo(taskNo)
                .operationType(ChainOperationTypeEnum.DEPLOY_TOKEN.getType())
                .projectId(projectId)
                .projectName(projectName)
                .projectSymbol(projectSymbol)
                .status(ChainTaskStatusEnum.PENDING.getStatus())
                .retryCount(0)
                .maxRetryCount(MAX_RETRY_COUNT)
                .build();

        chainOperationTaskService.createTask(task);
        log.info("[ChainOperationTaskHelper] 创建部署 Token 任务，taskNo: {}, 项目ID: {}", taskNo, projectId);

        return task;
    }

    /**
     * 创建或获取添加用户任务
     *
     * @param taskNo 任务编号
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param projectId 项目ID
     * @param projectName 项目名称
     * @param tokenAddress Token 地址
     * @param userId 用户ID
     * @param userAddress 用户钱包地址
     * @param countryCode 国家代码
     * @return 任务信息
     */
    public ChainOperationTaskDO createOrGetAddUserTask(String taskNo, Long orderId, String orderNo,
                                                       Long projectId, String projectName, String tokenAddress,
                                                       Long userId, String userAddress, Integer countryCode) {
        // 先查询任务是否已存在
        ChainOperationTaskDO existTask = chainOperationTaskService.getByTaskNo(taskNo);
        if (existTask != null) {
            return existTask;
        }

        // 创建新任务
        ChainOperationTaskDO task = ChainOperationTaskDO.builder()
                .taskNo(taskNo)
                .operationType(ChainOperationTypeEnum.ADD_USER_AND_ISSUE_CLAIM.getType())
                .orderId(orderId)
                .orderNo(orderNo)
                .projectId(projectId)
                .projectName(projectName)
                .tokenAddress(tokenAddress)
                .userId(userId)
                .userAddress(userAddress)
                .countryCode(countryCode)
                .status(ChainTaskStatusEnum.PENDING.getStatus())
                .retryCount(0)
                .maxRetryCount(MAX_RETRY_COUNT)
                .build();

        chainOperationTaskService.createTask(task);
        log.info("[ChainOperationTaskHelper] 创建添加用户任务，taskNo: {}, 订单ID: {}", taskNo, orderId);

        return task;
    }

    /**
     * 创建或获取铸造 Token 任务
     *
     * @param taskNo 任务编号
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param projectId 项目ID
     * @param projectName 项目名称
     * @param tokenAddress Token 地址
     * @param userId 用户ID
     * @param userAddress 用户钱包地址
     * @param mintAmount 铸造数量
     * @return 任务信息
     */
    public ChainOperationTaskDO createOrGetMintTokenTask(String taskNo, Long orderId, String orderNo,
                                                         Long projectId, String projectName, String tokenAddress,
                                                         Long userId, String userAddress, String mintAmount) {
        // 先查询任务是否已存在
        ChainOperationTaskDO existTask = chainOperationTaskService.getByTaskNo(taskNo);
        if (existTask != null) {
            return existTask;
        }

        // 创建新任务
        ChainOperationTaskDO task = ChainOperationTaskDO.builder()
                .taskNo(taskNo)
                .operationType(ChainOperationTypeEnum.MINT_TOKEN.getType())
                .orderId(orderId)
                .orderNo(orderNo)
                .projectId(projectId)
                .projectName(projectName)
                .tokenAddress(tokenAddress)
                .userId(userId)
                .userAddress(userAddress)
                .mintAmount(mintAmount)
                .status(ChainTaskStatusEnum.PENDING.getStatus())
                .retryCount(0)
                .maxRetryCount(MAX_RETRY_COUNT)
                .build();

        chainOperationTaskService.createTask(task);
        log.info("[ChainOperationTaskHelper] 创建铸造 Token 任务，taskNo: {}, 订单ID: {}", taskNo, orderId);

        return task;
    }
}
