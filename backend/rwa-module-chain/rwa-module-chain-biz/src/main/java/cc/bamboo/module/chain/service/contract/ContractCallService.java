package cc.bamboo.module.chain.service.contract;

import cc.bamboo.module.chain.config.Web3jConfig;
import cc.bamboo.module.chain.util.Web3jUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 合约调用服务
 * 提供通用的智能合约调用功能
 * 
 * @author Swolf
 */
@Service
@Slf4j
public class ContractCallService {
    
    @Resource
    private Web3j web3j;
    
    @Resource
    private Web3jConfig web3jConfig;
    
    /**
     * 调用合约方法（写操作）
     * 
     * @param credentials 凭证
     * @param contractAddress 合约地址
     * @param function 函数对象
     * @return 交易回执
     */
    public TransactionReceipt callContract(Credentials credentials, String contractAddress, 
            Function function) throws Exception {
        String encodedFunction = FunctionEncoder.encode(function);
        
        BigInteger nonce = Web3jUtil.getLatestNonce(web3j, credentials.getAddress());
        
        RawTransaction rawTransaction = Web3jUtil.createFunctionCallTransaction(
                nonce,
                BigInteger.valueOf(web3jConfig.getGasPrice()),
                BigInteger.valueOf(web3jConfig.getGasLimit()),
                contractAddress,
                encodedFunction
        );
        log.info("调用合约 {}，rawTransaction: {}", contractAddress, rawTransaction);
        
        // 使用带 chainId 的签名方式（EIP-155 重放保护）
        String txHash = Web3jUtil.sendRawTransaction(web3j, credentials, rawTransaction, web3jConfig.getChainId());
        //String txHash = "";
        log.info("交易已发送，txHash: {}", txHash);
        
        TransactionReceipt receipt = Web3jUtil.waitForReceipt(web3j, txHash);
        
        if (!receipt.isStatusOK()) {
            throw new RuntimeException("交易执行失败，txHash: " + txHash);
        }
        
        return receipt;
    }
    
    /**
     * 调用 IdFactory.createIdentityWithManagementKeys
     * 
     * @param credentials 凭证
     * @param factoryAddress IdFactory 合约地址
     * @param walletAddress 钱包地址
     * @param salt 盐值
     * @param managementKeys 管理密钥列表
     * @return 交易回执
     */
    public TransactionReceipt createIdentityWithManagementKeys(Credentials credentials,
            String factoryAddress, String walletAddress, String salt, 
            List<byte[]> managementKeys) throws Exception {
        
        // 构建 bytes32[] 参数
        DynamicArray<Bytes32> keysArray = new DynamicArray<>(
                Bytes32.class,
                managementKeys.stream()
                        .map(Bytes32::new)
                        .toArray(Bytes32[]::new)
        );
        
        Function function = new Function(
                "createIdentityWithManagementKeys",
                Arrays.asList(
                        new Address(walletAddress),
                        new Utf8String(salt),
                        keysArray
                ),
                Collections.emptyList()
        );
        
        return callContract(credentials, factoryAddress, function);
    }
    
    /**
     * 调用 Identity.addKey
     * 
     * @param credentials 凭证
     * @param identityAddress Identity 合约地址
     * @param keyHash 密钥哈希 (bytes32)
     * @param purpose 密钥用途 (1=MANAGEMENT, 2=ACTION, 3=CLAIM, 4=ENCRYPTION)
     * @param keyType 密钥类型 (1=ECDSA, 2=RSA)
     * @return 交易回执
     */
    public TransactionReceipt addKey(Credentials credentials, String identityAddress,
            byte[] keyHash, BigInteger purpose, BigInteger keyType) throws Exception {
        
        Function function = new Function(
                "addKey",
                Arrays.asList(
                        new Bytes32(keyHash),
                        new Uint256(purpose),
                        new Uint256(keyType)
                ),
                Collections.singletonList(new TypeReference<Bool>() {})
        );
        
        return callContract(credentials, identityAddress, function);
    }
    
    /**
     * 调用 IdentityRegistry.registerIdentity
     * 
     * @param credentials 凭证
     * @param registryAddress IdentityRegistry 合约地址
     * @param userAddress 用户钱包地址
     * @param identityAddress 用户 Identity 合约地址
     * @param countryCode 国家代码
     * @return 交易回执
     */
    public TransactionReceipt registerIdentity(Credentials credentials, String registryAddress,
            String userAddress, String identityAddress, int countryCode) throws Exception {
        
        Function function = new Function(
                "registerIdentity",
                Arrays.asList(
                        new Address(userAddress),
                        new Address(identityAddress),
                        new Uint16(countryCode)
                ),
                Collections.emptyList()
        );
        
        return callContract(credentials, registryAddress, function);
    }

    public TransactionReceipt deleteIdentity(Credentials credentials, String registryAddress,
                                               String userAddress) throws Exception {

        Function function = new Function(
                "deleteIdentity",
                Arrays.asList(
                        new Address(userAddress)
                ),
                Collections.emptyList()
        );

        return callContract(credentials, registryAddress, function);
    }
    
    /**
     * 调用 Identity.addClaim
     * 
     * @param credentials 凭证
     * @param identityAddress Identity 合约地址
     * @param topic 声明主题
     * @param scheme 签名方案
     * @param issuer 签发者地址
     * @param signature 签名
     * @param data 数据
     * @param uri URI
     * @return 交易回执
     */
    public TransactionReceipt addClaim(Credentials credentials, String identityAddress,
            BigInteger topic, BigInteger scheme, String issuer, byte[] signature,
            byte[] data, String uri) throws Exception {
        
        Function function = new Function(
                "addClaim",
                Arrays.asList(
                        new Uint256(topic),
                        new Uint256(scheme),
                        new Address(issuer),
                        new DynamicBytes(signature),
                        new DynamicBytes(data),
                        new Utf8String(uri)
                ),
                Collections.singletonList(new TypeReference<Bytes32>() {})
        );
        
        return callContract(credentials, identityAddress, function);
    }
    
    /**
     * 调用 Ownable.transferOwnership
     * 
     * @param credentials 凭证
     * @param contractAddress 合约地址
     * @param newOwner 新所有者地址
     * @return 交易回执
     */
    public TransactionReceipt transferOwnership(Credentials credentials, String contractAddress,
            String newOwner) throws Exception {
        
        Function function = new Function(
                "transferOwnership",
                Collections.singletonList(new Address(newOwner)),
                Collections.emptyList()
        );
        
        return callContract(credentials, contractAddress, function);
    }
    
    /**
     * 调用 Token.mint 发行代币
     * 
     * @param credentials 凭证（必须是 Token Agent）
     * @param tokenAddress Token 合约地址
     * @param toAddress 接收地址
     * @param amount 发行数量
     * @return 交易回执
     */
    public TransactionReceipt mint(Credentials credentials, String tokenAddress,
            String toAddress, BigInteger amount) throws Exception {
        
        Function function = new Function(
                "mint",
                Arrays.asList(
                        new Address(toAddress),
                        new Uint256(amount)
                ),
                Collections.emptyList()
        );
        
        return callContract(credentials, tokenAddress, function);
    }
    
    /**
     * 调用 Token.burn 销毁代币
     * 
     * @param credentials 凭证（必须是 Token Agent）
     * @param tokenAddress Token 合约地址
     * @param userAddress 用户地址
     * @param amount 销毁数量
     * @return 交易回执
     */
    public TransactionReceipt burn(Credentials credentials, String tokenAddress,
            String userAddress, BigInteger amount) throws Exception {
        
        Function function = new Function(
                "burn",
                Arrays.asList(
                        new Address(userAddress),
                        new Uint256(amount)
                ),
                Collections.emptyList()
        );
        
        return callContract(credentials, tokenAddress, function);
    }
    
    /**
     * 调用 Token.forcedTransfer 强制转账
     * 
     * @param credentials 凭证（必须是 Token Agent）
     * @param tokenAddress Token 合约地址
     * @param fromAddress 发送地址
     * @param toAddress 接收地址
     * @param amount 转账数量
     * @return 交易回执
     */
    public TransactionReceipt forcedTransfer(Credentials credentials, String tokenAddress,
            String fromAddress, String toAddress, BigInteger amount) throws Exception {
        
        Function function = new Function(
                "forcedTransfer",
                Arrays.asList(
                        new Address(fromAddress),
                        new Address(toAddress),
                        new Uint256(amount)
                ),
                Collections.singletonList(new TypeReference<Bool>() {})
        );
        
        return callContract(credentials, tokenAddress, function);
    }
    
    /**
     * 调用 Token.pause 暂停代币
     * 
     * @param credentials 凭证（必须是 Token Agent）
     * @param tokenAddress Token 合约地址
     * @return 交易回执
     */
    public TransactionReceipt pause(Credentials credentials, String tokenAddress) throws Exception {
        
        Function function = new Function(
                "pause",
                Collections.emptyList(),
                Collections.emptyList()
        );
        
        return callContract(credentials, tokenAddress, function);
    }
    
    /**
     * 调用 Token.unpause 恢复代币
     * 
     * @param credentials 凭证（必须是 Token Agent）
     * @param tokenAddress Token 合约地址
     * @return 交易回执
     */
    public TransactionReceipt unpause(Credentials credentials, String tokenAddress) throws Exception {
        
        Function function = new Function(
                "unpause",
                Collections.emptyList(),
                Collections.emptyList()
        );
        
        return callContract(credentials, tokenAddress, function);
    }
    
    /**
     * 调用 Token.freezePartialTokens 冻结部分代币
     * 
     * @param credentials 凭证（必须是 Token Agent）
     * @param tokenAddress Token 合约地址
     * @param userAddress 用户地址
     * @param amount 冻结数量
     * @return 交易回执
     */
    public TransactionReceipt freezePartialTokens(Credentials credentials, String tokenAddress,
            String userAddress, BigInteger amount) throws Exception {
        
        Function function = new Function(
                "freezePartialTokens",
                Arrays.asList(
                        new Address(userAddress),
                        new Uint256(amount)
                ),
                Collections.emptyList()
        );
        
        return callContract(credentials, tokenAddress, function);
    }
    
    /**
     * 调用 Token.unfreezePartialTokens 解冻部分代币
     * 
     * @param credentials 凭证（必须是 Token Agent）
     * @param tokenAddress Token 合约地址
     * @param userAddress 用户地址
     * @param amount 解冻数量
     * @return 交易回执
     */
    public TransactionReceipt unfreezePartialTokens(Credentials credentials, String tokenAddress,
            String userAddress, BigInteger amount) throws Exception {
        
        Function function = new Function(
                "unfreezePartialTokens",
                Arrays.asList(
                        new Address(userAddress),
                        new Uint256(amount)
                ),
                Collections.emptyList()
        );
        
        return callContract(credentials, tokenAddress, function);
    }
    
    /**
     * 从交易回执中解析 WalletLinked 事件获取 Identity 地址
     * 
     * @param receipt 交易回执
     * @return Identity 合约地址
     */
    public String parseWalletLinkedEvent(TransactionReceipt receipt) {
        // WalletLinked(address indexed wallet, address indexed identity)
        String eventSignature = EventEncoder.encode(
                new Event("WalletLinked", Arrays.asList(
                        new TypeReference<Address>(true) {},
                        new TypeReference<Address>(true) {}
                ))
        );
        
        for (Log logEntry : receipt.getLogs()) {
            if (logEntry.getTopics().size() >= 3 && 
                    logEntry.getTopics().get(0).equals(eventSignature)) {
                // indexed 参数在 topics 中，identity 是第三个 topic
                String identityTopic = logEntry.getTopics().get(2);
                // 从 topic 中提取地址（去掉前面的 0 填充）
                return "0x" + identityTopic.substring(26);
            }
        }
        
        throw new RuntimeException("未找到 WalletLinked 事件");
    }
    
    /**
     * 从交易回执中解析 TREXSuiteDeployed 事件
     * 
     * @param receipt 交易回执
     * @return 部署的合约地址数组 [token, ir, irs, tir, ctr, mc]
     */
    public String[] parseTREXSuiteDeployedEvent(TransactionReceipt receipt) {
        // TREXSuiteDeployed(address indexed token, address ir, address irs, 
        //                   address tir, address ctr, address mc, string indexed salt)
        String eventSignature = EventEncoder.encode(
                new Event("TREXSuiteDeployed", Arrays.asList(
                        new TypeReference<Address>(true) {},  // token (indexed)
                        new TypeReference<Address>() {},      // ir
                        new TypeReference<Address>() {},      // irs
                        new TypeReference<Address>() {},      // tir
                        new TypeReference<Address>() {},      // ctr
                        new TypeReference<Address>() {},      // mc
                        new TypeReference<Utf8String>(true) {} // salt (indexed)
                ))
        );
        
        for (Log logEntry : receipt.getLogs()) {
            if (!logEntry.getTopics().isEmpty() && 
                    logEntry.getTopics().get(0).equals(eventSignature)) {
                // token 地址在 topics[1]
                String tokenAddress = "0x" + logEntry.getTopics().get(1).substring(26);
                
                // 其他地址在 data 中
                @SuppressWarnings({"rawtypes", "unchecked"})
                List<TypeReference<Type>> outputParams;
                try {
                    outputParams = Arrays.asList(
                            TypeReference.makeTypeReference("address"),
                            TypeReference.makeTypeReference("address"),
                            TypeReference.makeTypeReference("address"),
                            TypeReference.makeTypeReference("address"),
                            TypeReference.makeTypeReference("address")
                    );
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("无法创建 TypeReference", e);
                }
                @SuppressWarnings("rawtypes")
                List<Type> decoded = FunctionReturnDecoder.decode(logEntry.getData(), outputParams);
                
                return new String[] {
                        tokenAddress,
                        ((Address) decoded.get(0)).getValue(),
                        ((Address) decoded.get(1)).getValue(),
                        ((Address) decoded.get(2)).getValue(),
                        ((Address) decoded.get(3)).getValue(),
                        ((Address) decoded.get(4)).getValue()
                };
            }
        }
        
        throw new RuntimeException("未找到 TREXSuiteDeployed 事件");
    }
}
