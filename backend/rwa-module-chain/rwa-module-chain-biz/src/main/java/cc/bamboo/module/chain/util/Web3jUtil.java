package cc.bamboo.module.chain.util;

import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Web3j 工具类
 * 提供区块链交互的通用方法
 * 
 * @author Swolf
 */
@Slf4j
public class Web3jUtil {
    
    private Web3jUtil() {
        // 工具类不允许实例化
    }
    
    /**
     * 获取最新的 nonce
     * 使用 'pending' 状态包含未确认的交易
     * 
     * @param web3j Web3j 实例
     * @param address 钱包地址
     * @return nonce 值
     */
    public static BigInteger getLatestNonce(Web3j web3j, String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount = web3j
                .ethGetTransactionCount(address, DefaultBlockParameterName.PENDING)
                .send();
        return ethGetTransactionCount.getTransactionCount();
    }
    
    /**
     * 等待交易确认
     * 
     * @param web3j Web3j 实例
     * @param txHash 交易哈希
     * @param maxAttempts 最大尝试次数
     * @param sleepDuration 每次尝试间隔（毫秒）
     * @return 交易回执
     */
    public static TransactionReceipt waitForReceipt(Web3j web3j, String txHash, 
            int maxAttempts, long sleepDuration) throws Exception {
        Optional<TransactionReceipt> receipt;
        int attempts = 0;
        
        do {
            Thread.sleep(sleepDuration);
            EthGetTransactionReceipt ethGetTransactionReceipt = web3j
                    .ethGetTransactionReceipt(txHash)
                    .send();
            receipt = ethGetTransactionReceipt.getTransactionReceipt();
            attempts++;
        } while (!receipt.isPresent() && attempts < maxAttempts);
        
        if (!receipt.isPresent()) {
            throw new RuntimeException("交易回执未找到，超时时间: " + (maxAttempts * sleepDuration / 1000) + " 秒");
        }
        
        return receipt.get();
    }
    
    /**
     * 等待交易确认（默认参数）
     * 默认最多等待 60 秒
     * 
     * @param web3j Web3j 实例
     * @param txHash 交易哈希
     * @return 交易回执
     */
    public static TransactionReceipt waitForReceipt(Web3j web3j, String txHash) throws Exception {
        return waitForReceipt(web3j, txHash, 60, 1000);
    }
    
    /**
     * 发送原始交易（不带 chainId，已废弃）
     * 
     * @param web3j Web3j 实例
     * @param credentials 凭证
     * @param rawTransaction 原始交易
     * @return 交易哈希
     * @deprecated 请使用 {@link #sendRawTransaction(Web3j, Credentials, RawTransaction, long)} 方法
     */
    @Deprecated
    public static String sendRawTransaction(Web3j web3j, Credentials credentials, 
            RawTransaction rawTransaction) throws Exception {
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        
        if (ethSendTransaction.hasError()) {
            throw new RuntimeException("发送交易失败: " + ethSendTransaction.getError().getMessage());
        }
        
        return ethSendTransaction.getTransactionHash();
    }
    
    /**
     * 发送原始交易（带 chainId，支持 EIP-155 重放保护）
     * 
     * @param web3j Web3j 实例
     * @param credentials 凭证
     * @param rawTransaction 原始交易
     * @param chainId 链 ID
     * @return 交易哈希
     */
    public static String sendRawTransaction(Web3j web3j, Credentials credentials, 
            RawTransaction rawTransaction, long chainId) throws Exception {
        // 使用带 chainId 的签名方式（EIP-155）
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        
        if (ethSendTransaction.hasError()) {
            throw new RuntimeException("发送交易失败: " + ethSendTransaction.getError().getMessage());
        }
        
        return ethSendTransaction.getTransactionHash();
    }
    
    /**
     * 创建合约部署交易
     * 
     * @param nonce nonce 值
     * @param gasPrice gas 价格
     * @param gasLimit gas 限制
     * @param data 合约字节码 + 构造函数参数
     * @return 原始交易
     */
    public static RawTransaction createContractTransaction(BigInteger nonce, BigInteger gasPrice,
            BigInteger gasLimit, String data) {
        return RawTransaction.createContractTransaction(
                nonce,
                gasPrice,
                gasLimit,
                BigInteger.ZERO,
                data
        );
    }
    
    /**
     * 创建合约调用交易
     * 
     * @param nonce nonce 值
     * @param gasPrice gas 价格
     * @param gasLimit gas 限制
     * @param to 目标合约地址
     * @param data 调用数据
     * @return 原始交易
     */
    public static RawTransaction createFunctionCallTransaction(BigInteger nonce, BigInteger gasPrice,
            BigInteger gasLimit, String to, String data) {
        return RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                to,
                BigInteger.ZERO,
                data
        );
    }
    
    /**
     * 使用私钥签名消息
     * 
     * @param credentials 凭证
     * @param message 消息（bytes32）
     * @return 签名（十六进制字符串）
     */
    public static String signMessage(Credentials credentials, byte[] message) {
        Sign.SignatureData signatureData = Sign.signPrefixedMessage(message, credentials.getEcKeyPair());
        
        // 将签名数据转换为十六进制字符串
        byte[] r = signatureData.getR();
        byte[] s = signatureData.getS();
        byte[] v = signatureData.getV();
        
        byte[] signature = new byte[65];
        System.arraycopy(r, 0, signature, 0, 32);
        System.arraycopy(s, 0, signature, 32, 32);
        signature[64] = v[0];
        
        return Numeric.toHexString(signature);
    }
    
    /**
     * 从私钥创建凭证
     * 
     * @param privateKey 私钥（十六进制字符串）
     * @return 凭证
     */
    public static Credentials createCredentials(String privateKey) {
        return Credentials.create(privateKey);
    }
    
    /**
     * 验证以太坊地址格式
     * 
     * @param address 地址
     * @return 是否有效
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        // 检查是否以 0x 开头且长度为 42
        return address.matches("^0x[a-fA-F0-9]{40}$");
    }
    
    /**
     * 将地址转换为校验和格式
     * 
     * @param address 地址
     * @return 校验和格式的地址
     */
    public static String toChecksumAddress(String address) {
        return org.web3j.crypto.Keys.toChecksumAddress(address);
    }
}
