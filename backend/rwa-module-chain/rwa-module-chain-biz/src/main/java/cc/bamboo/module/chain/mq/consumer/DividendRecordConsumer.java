package cc.bamboo.module.chain.mq.consumer;

import cc.bamboo.module.chain.dal.dataobject.chainoperationlog.ChainOperationLogDO;
import cc.bamboo.module.chain.dal.mysql.chainoperationlog.ChainOperationLogMapper;
import cc.bamboo.module.chain.mq.message.DividendRecordMessage;
import cc.bamboo.module.chain.mq.producer.DividendRecordProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 分红记录消息消费者
 * 负责异步检查交易回执并更新状态
 *
 * @author Swolf
 */
@Component
@Slf4j
@RabbitListener(bindings = @QueueBinding(value = @Queue(name = "chain.dividend.check.queue", durable = "true"), exchange = @Exchange(name = DividendRecordProducer.EXCHANGE_NAME, type = ExchangeTypes.DIRECT, delayed = "true", durable = "true"), key = DividendRecordProducer.ROUTING_KEY))
public class DividendRecordConsumer {

    @Resource
    private ChainOperationLogMapper chainOperationLogMapper;

    @Resource
    private Web3j web3j;

    @Resource
    private DividendRecordProducer dividendRecordProducer;

    @RabbitHandler
    public void onMessage(DividendRecordMessage message) {
        Long logId = message.getLogId();
        String txHash = message.getTransactionHash();
        int retryCount = message.getRetryCount() != null ? message.getRetryCount() : 0;

        log.info("[DividendRecordConsumer] 收到检查消息，logId: {}, txHash: {}, retryCount: {}",
                logId, txHash, retryCount);

        try {
            // 1. 获取日志记录
            ChainOperationLogDO logDO = chainOperationLogMapper.selectById(logId);
            if (logDO == null) {
                log.error("[DividendRecordConsumer] 日志不存在，logId: {}", logId);
                return;
            }

            // 2. 检查状态是否已经终结 (1-成功, 2-失败)
            if (logDO.getStatus() != 0) {
                log.info("[DividendRecordConsumer] 日志状态已更新，跳过检查，logId: {}, status: {}",
                        logId, logDO.getStatus());
                return;
            }

            // 3. 查询交易回执
            Optional<TransactionReceipt> receiptOptional = web3j.ethGetTransactionReceipt(txHash)
                    .send()
                    .getTransactionReceipt();

            if (receiptOptional.isPresent()) {
                TransactionReceipt receipt = receiptOptional.get();
                if (receipt.isStatusOK()) {
                    // 交易成功
                    logDO.setStatus(1); // 成功
                    logDO.setBlockNumber(receipt.getBlockNumber().longValue());
                    logDO.setGasUsed(receipt.getGasUsed().longValue());
                    chainOperationLogMapper.updateById(logDO);
                    log.info("[DividendRecordConsumer] 交易确认成功，logId: {}, txHash: {}", logId, txHash);
                } else {
                    // 交易失败
                    logDO.setStatus(2); // 失败
                    logDO.setErrorMessage("交易失败，状态码: " + receipt.getStatus());
                    chainOperationLogMapper.updateById(logDO);
                    log.error("[DividendRecordConsumer] 交易确认失败，logId: {}, txHash: {}", logId, txHash);
                }
            } else {
                // 回执未找到，需要重试
                handleRetry(message, retryCount, "交易回执未找到");
            }

        } catch (Exception e) {
            log.error("[DividendRecordConsumer] 处理消息异常，logId: {}, 错误: {}", logId, e.getMessage(), e);
            // 异常情况也进行重试
            handleRetry(message, retryCount, "处理异常: " + e.getMessage());
        }
    }

    private void handleRetry(DividendRecordMessage message, int currentRetryCount, String reason) {
        int nextRetryCount = currentRetryCount + 1;
        int maxRetry = dividendRecordProducer.getMaxRetryCount();

        if (nextRetryCount <= maxRetry) {
            // 发送延时重试消息
            message.setRetryCount(nextRetryCount);
            dividendRecordProducer.sendCheckMessage(message);
            log.info("[DividendRecordConsumer] 需要重试，原因: {}，logId: {}, 下次重试次数: {}/{}",
                    reason, message.getLogId(), nextRetryCount, maxRetry);
        } else {
            // 超过最大重试次数，标记为失败（超时）
            log.warn("[DividendRecordConsumer] 达到最大重试次数，标记为失败，logId: {}, txHash: {}",
                    message.getLogId(), message.getTransactionHash());

            ChainOperationLogDO logDO = chainOperationLogMapper.selectById(message.getLogId());
            if (logDO != null && logDO.getStatus() == 0) {
                logDO.setStatus(2); // 失败
                logDO.setErrorMessage("交易确认超时，原因: " + reason);
                chainOperationLogMapper.updateById(logDO);
            }
        }
    }
}
