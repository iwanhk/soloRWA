package cc.bamboo.module.chain.service.chainoperationtask;

import cc.bamboo.module.chain.dal.dataobject.chainoperationtask.ChainOperationTaskDO;
import cc.bamboo.module.chain.dal.mysql.chainoperationtask.ChainOperationTaskMapper;
import cc.bamboo.module.chain.enums.ChainTaskStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 链操作任务服务实现
 *
 * @author Swolf
 */
@Service
@Slf4j
public class ChainOperationTaskServiceImpl implements ChainOperationTaskService {

    @Resource
    private ChainOperationTaskMapper chainOperationTaskMapper;

    @Override
    public ChainOperationTaskDO getByTaskNo(String taskNo) {
        return chainOperationTaskMapper.selectOne(ChainOperationTaskDO::getTaskNo, taskNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(ChainOperationTaskDO task) {
        chainOperationTaskMapper.insert(task);
        return task.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskToProcessing(String taskNo) {
        ChainOperationTaskDO task = getByTaskNo(taskNo);
        if (task == null) {
            log.warn("[ChainOperationTaskService] 任务不存在，taskNo: {}", taskNo);
            return;
        }

        task.setStatus(ChainTaskStatusEnum.PROCESSING.getStatus());
        task.setUpdateTime(LocalDateTime.now());
        chainOperationTaskMapper.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskToSuccess(String taskNo, String executionSteps,String hash) {
        ChainOperationTaskDO task = getByTaskNo(taskNo);
        if (task == null) {
            log.warn("[ChainOperationTaskService] 任务不存在，taskNo: {}", taskNo);
            return;
        }

        task.setStatus(ChainTaskStatusEnum.SUCCESS.getStatus());
        task.setExecutionSteps(executionSteps);
        task.setCompletedTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        task.setTxHash(hash);
        chainOperationTaskMapper.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskToFailed(String taskNo, Integer retryCount, String errorMessage, String executionSteps) {
        ChainOperationTaskDO task = getByTaskNo(taskNo);
        if (task == null) {
            log.warn("[ChainOperationTaskService] 任务不存在，taskNo: {}", taskNo);
            return;
        }

        task.setStatus(ChainTaskStatusEnum.FAILED.getStatus());
        task.setRetryCount(retryCount);
        task.setErrorMessage(errorMessage);
        task.setExecutionSteps(executionSteps);
        task.setUpdateTime(LocalDateTime.now());
        chainOperationTaskMapper.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTokenAddress(String taskNo, String tokenAddress) {
        ChainOperationTaskDO task = getByTaskNo(taskNo);
        if (task == null) {
            log.warn("[ChainOperationTaskService] 任务不存在，taskNo: {}", taskNo);
            return;
        }

        task.setTokenAddress(tokenAddress);
        task.setUpdateTime(LocalDateTime.now());
        chainOperationTaskMapper.updateById(task);
    }
}
