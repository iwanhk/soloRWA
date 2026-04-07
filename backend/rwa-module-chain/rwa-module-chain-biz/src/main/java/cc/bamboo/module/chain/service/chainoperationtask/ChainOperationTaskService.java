package cc.bamboo.module.chain.service.chainoperationtask;

import cc.bamboo.module.chain.dal.dataobject.chainoperationtask.ChainOperationTaskDO;

/**
 * 链操作任务服务接口
 *
 * @author Swolf
 */
public interface ChainOperationTaskService {

    /**
     * 根据任务编号查询任务
     *
     * @param taskNo 任务编号
     * @return 任务信息
     */
    ChainOperationTaskDO getByTaskNo(String taskNo);

    /**
     * 创建任务
     *
     * @param task 任务信息
     * @return 任务ID
     */
    Long createTask(ChainOperationTaskDO task);

    /**
     * 更新任务状态为处理中
     *
     * @param taskNo 任务编号
     */
    void updateTaskToProcessing(String taskNo);

    /**
     * 更新任务为成功
     *
     * @param taskNo 任务编号
     * @param executionSteps 执行步骤
     */
    void updateTaskToSuccess(String taskNo, String executionSteps,String hash);

    /**
     * 更新任务为失败
     *
     * @param taskNo 任务编号
     * @param retryCount 重试次数
     * @param errorMessage 错误信息
     * @param executionSteps 执行步骤
     */
    void updateTaskToFailed(String taskNo, Integer retryCount, String errorMessage, String executionSteps);

    /**
     * 更新任务的 Token 地址
     *
     * @param taskNo 任务编号
     * @param tokenAddress Token 地址
     */
    void updateTokenAddress(String taskNo, String tokenAddress);
}
