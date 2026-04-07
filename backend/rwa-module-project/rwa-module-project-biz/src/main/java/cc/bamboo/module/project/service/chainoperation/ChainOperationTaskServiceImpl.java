package cc.bamboo.module.project.service.chainoperation;

import cc.bamboo.module.project.mq.message.OrderAuditPassMessage;
import cc.bamboo.module.project.mq.message.OrderCreateMessage;
import cc.bamboo.module.project.mq.producer.OrderAuditPassProducer;
import cc.bamboo.module.project.mq.producer.OrderCreateProducer;
import cc.bamboo.module.project.mq.producer.ProjectAuditPassProducer;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 链操作任务服务实现
 * 负责创建链操作任务并发送消息到 chain 模块
 *
 * @author Swolf
 */
@Service
@Slf4j
public class ChainOperationTaskServiceImpl implements ChainOperationTaskService {

    @Resource
    private ProjectAuditPassProducer projectAuditPassProducer;

    @Resource
    private OrderCreateProducer orderCreateProducer;

    @Resource
    private OrderAuditPassProducer orderAuditPassProducer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createDeployTokenTask(Long projectId, String projectName, String projectSymbol) {
        // 生成任务编号
        String taskNo = generateTaskNo("DEPLOY");

        log.info("[ChainOperationTaskService] 创建部署 Token 任务，taskNo: {}, 项目ID: {}, 项目名称: {},Symbol: {} ",
                taskNo, projectId, projectName, projectSymbol);

        try {
            // 发送消息到 chain 模块
            projectAuditPassProducer.sendProjectAuditPassMessage(taskNo, projectId, projectName, projectSymbol);

            log.info("[ChainOperationTaskService] 部署 Token 任务创建成功，taskNo: {}, 项目ID: {}",
                    taskNo, projectId);

            return taskNo;

        } catch (Exception e) {
            log.error("[ChainOperationTaskService] 部署 Token 任务创建失败，taskNo: {}, 项目ID: {}, 错误: {}",
                    taskNo, projectId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createAddUserTask(Long orderId, String orderNo, Long projectId, String projectName,
                                   String tokenAddress, Long userId, String userAddress, Integer countryCode) {
        // 生成任务编号
        String taskNo = generateTaskNo("ADDUSER");

        log.info("[ChainOperationTaskService] 创建添加用户任务，taskNo: {}, 订单ID: {}, 用户ID: {}",
                taskNo, orderId, userId);

        try {
            // 构建消息
            OrderCreateMessage message = new OrderCreateMessage();
            message.setTaskNo(taskNo);
            message.setOrderId(orderId);
            message.setOrderNo(orderNo);
            message.setProjectId(projectId);
            message.setProjectName(projectName);
            message.setTokenAddress(tokenAddress);
            message.setUserId(userId);
            message.setUserAddress(userAddress);
            message.setCountryCode(countryCode);
            message.setRetryCount(0);

            // 发送消息到 chain 模块
            orderCreateProducer.sendOrderCreateMessage(message);

            log.info("[ChainOperationTaskService] 添加用户任务创建成功，taskNo: {}, 订单ID: {}",
                    taskNo, orderId);

            return taskNo;

        } catch (Exception e) {
            log.error("[ChainOperationTaskService] 添加用户任务创建失败，taskNo: {}, 订单ID: {}, 错误: {}",
                    taskNo, orderId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createMintTokenTask(Long orderId, String orderNo, Long projectId, String projectName,
                                     String tokenAddress, Long userId, String userAddress, String mintAmount) {
        // 生成任务编号
        String taskNo = generateTaskNo("MINT");

        log.info("[ChainOperationTaskService] 创建铸造 Token 任务，taskNo: {}, 订单ID: {}, 用户ID: {}, 数量: {}",
                taskNo, orderId, userId, mintAmount);

        try {
            // 构建消息
            OrderAuditPassMessage message = new OrderAuditPassMessage();
            message.setTaskNo(taskNo);
            message.setOrderId(orderId);
            message.setOrderNo(orderNo);
            message.setProjectId(projectId);
            message.setProjectName(projectName);
            message.setTokenAddress(tokenAddress);
            message.setUserId(userId);
            message.setUserAddress(userAddress);
            message.setMintAmount(mintAmount);
            message.setRetryCount(0);

            // 发送消息到 chain 模块
            orderAuditPassProducer.sendOrderAuditPassMessage(message);

            log.info("[ChainOperationTaskService] 铸造 Token 任务创建成功，taskNo: {}, 订单ID: {}",
                    taskNo, orderId);

            return taskNo;

        } catch (Exception e) {
            log.error("[ChainOperationTaskService] 铸造 Token 任务创建失败，taskNo: {}, 订单ID: {}, 错误: {}",
                    taskNo, orderId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 生成任务编号
     * 格式：前缀_时间戳_随机数
     *
     * @param prefix 前缀
     * @return 任务编号
     */
    private String generateTaskNo(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + IdUtil.fastSimpleUUID().substring(0, 8);
    }
}
