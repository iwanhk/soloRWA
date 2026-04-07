package cc.bamboo.module.chain.service.dividend;

import cc.bamboo.module.chain.config.ContractConfig;
import cc.bamboo.module.chain.config.Web3jConfig;
import cc.bamboo.module.chain.dal.dataobject.blockchainaddresses.BlockchainAddressesDO;
import cc.bamboo.module.chain.dal.dataobject.chainoperationlog.ChainOperationLogDO;
import cc.bamboo.module.chain.dal.mysql.blockchainaddresses.BlockchainAddressesMapper;
import cc.bamboo.module.chain.dal.mysql.chainoperationlog.ChainOperationLogMapper;
import cc.bamboo.module.chain.api.dividend.dto.DividendRecordReqDTO;
import cc.bamboo.module.chain.api.dividend.dto.DividendRecordRespDTO;
import cc.bamboo.module.chain.mq.message.DividendRecordMessage;
import cc.bamboo.module.chain.mq.producer.DividendRecordProducer;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 分红记录上链服务实现
 *
 * @author Swolf
 */
@Service
@Slf4j
public class DividendRecordServiceImpl implements DividendRecordService {

        @Resource
        private Web3j web3j;

        @Resource
        private Web3jConfig web3jConfig;

        @Resource
        private ContractConfig contractConfig;

        @Resource
        private BlockchainAddressesMapper blockchainAddressesMapper;

        @Resource
        private ChainOperationLogMapper chainOperationLogMapper;

        @Resource
        private DividendRecordProducer dividendRecordProducer;

        @Override
        public DividendRecordRespDTO recordDividend(DividendRecordReqDTO reqDTO) {
                DividendRecordRespDTO respDTO = new DividendRecordRespDTO();
                respDTO.setProjectId(reqDTO.getProjectId());
                respDTO.setDate(reqDTO.getDate());
                respDTO.setSuccess(false);

                // 创建操作日志
                ChainOperationLogDO operationLog = ChainOperationLogDO.builder()
                                .operationType("DIVIDEND_RECORD")
                                .businessId(reqDTO.getProjectId())
                                .businessData(JSONUtil.toJsonStr(reqDTO))
                                .contractAddress(contractConfig.getDividendRecordAddress())
                                .status(0) // 待处理
                                .revenueDate(reqDTO.getDate())
                                .build();
                chainOperationLogMapper.insert(operationLog);

                try {
                        // 获取 deployer 凭证
                        Credentials deployerCredentials = getDeployerCredentials();
                        operationLog.setOperatorAddress(deployerCredentials.getAddress());

                        // 构建合约调用参数
                        Function function = new Function(
                                        "recordDividend",
                                        Arrays.asList(
                                                        new Utf8String(reqDTO.getDate().format(DateTimeFormatter.ISO_DATE)),
                                                        new Uint256(BigInteger.valueOf(reqDTO.getProjectId())),
                                                        new Uint256(reqDTO.getAmount()),
                                                        new Utf8String(reqDTO.getCurrency()),
                                                        new DynamicArray<>(Address.class,
                                                                        reqDTO.getAddresses().stream()
                                                                                        .map(Address::new)
                                                                                        .collect(Collectors.toList())),
                                                        new DynamicArray<>(Uint256.class,
                                                                        reqDTO.getShares().stream()
                                                                                        .map(Uint256::new)
                                                                                        .collect(Collectors.toList()))),
                                        Collections.emptyList());

                        String encodedFunction = FunctionEncoder.encode(function);

                        // 创建交易管理器
                        TransactionManager transactionManager = new RawTransactionManager(
                                        web3j,
                                        deployerCredentials,
                                        web3jConfig.getChainId());

                        // 发送交易
                        log.info("[recordDividend] 开始记录分红到链上，项目ID: {}, 日期: {}, 金额: {}, 币种: {}",
                                        reqDTO.getProjectId(), reqDTO.getDate(), reqDTO.getAmount(),
                                        reqDTO.getCurrency());

                        org.web3j.protocol.core.methods.response.EthSendTransaction ethSendTransaction = transactionManager
                                        .sendTransaction(
                                                        DefaultGasProvider.GAS_PRICE,
                                                        DefaultGasProvider.GAS_LIMIT,
                                                        contractConfig.getDividendRecordAddress(),
                                                        encodedFunction,
                                                        BigInteger.ZERO);

                        String transactionHash = ethSendTransaction.getTransactionHash();
                        if (transactionHash == null) {
                                respDTO.setErrorMessage("交易发送失败: " + ethSendTransaction.getError().getMessage());

                                // 更新操作日志为失败
                                operationLog.setStatus(2); // 失败
                                operationLog.setErrorMessage(ethSendTransaction.getError().getMessage());
                                chainOperationLogMapper.updateById(operationLog);

                                log.error("[recordDividend] 交易发送失败: {}", ethSendTransaction.getError().getMessage());
                                return respDTO;
                        }

                        // 更新日志记录交易哈希（状态保持为0-待处理）
                        operationLog.setTransactionHash(transactionHash);
                        chainOperationLogMapper.updateById(operationLog);

                        // 发送延时检查消息
                        DividendRecordMessage message = DividendRecordMessage.builder()
                                        .logId(operationLog.getId())
                                        .transactionHash(transactionHash)
                                        .retryCount(0)
                                        .build();
                        dividendRecordProducer.sendCheckMessage(message);

                        // 立即返回成功状态
                        respDTO.setSuccess(true);
                        respDTO.setTransactionHash(transactionHash);
                        log.info("[recordDividend] 交易已发送，等待异步确认，projectId: {}, txHash: {}",
                                        reqDTO.getProjectId(), transactionHash);

                } catch (Exception e) {
                        respDTO.setErrorMessage(e.getMessage());

                        // 更新操作日志为失败
                        operationLog.setStatus(2); // 失败
                        operationLog.setErrorMessage(e.getMessage());
                        chainOperationLogMapper.updateById(operationLog);

                        log.error("[recordDividend] 分红记录上链异常，项目ID: {}, 错误: {}",
                                        reqDTO.getProjectId(), e.getMessage(), e);
                }

                return respDTO;
        }

        /**
         * 获取 deployer 凭证
         */
        private Credentials getDeployerCredentials() {
                BlockchainAddressesDO deployer = blockchainAddressesMapper.selectOne(
                                BlockchainAddressesDO::getName, "deployer");
                if (deployer == null) {
                        throw new RuntimeException("未找到 deployer 钱包，请先创建名为 'deployer' 的钱包");
                }
                return Credentials.create(deployer.getPrivateKey());
        }
}
