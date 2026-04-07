package cc.bamboo.module.chain.service.identity;

import cc.bamboo.module.chain.config.Web3jConfig;
import cc.bamboo.module.chain.constant.ContractTypes;
import cc.bamboo.module.chain.constant.IdentityConstants;
import cc.bamboo.module.chain.dal.dataobject.addressidentities.AddressIdentitiesDO;
import cc.bamboo.module.chain.dal.dataobject.blockchainaddresses.BlockchainAddressesDO;
import cc.bamboo.module.chain.dal.dataobject.claimissueridentities.ClaimIssuerIdentitiesDO;
import cc.bamboo.module.chain.dal.dataobject.contractdeployments.ContractDeploymentsDO;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.module.chain.dal.dataobject.useridentities.UserIdentitiesDO;
import cc.bamboo.module.chain.dal.mysql.addressidentities.AddressIdentitiesMapper;
import cc.bamboo.module.chain.dal.mysql.blockchainaddresses.BlockchainAddressesMapper;
import cc.bamboo.module.chain.dal.mysql.claimissueridentities.ClaimIssuerIdentitiesMapper;
import cc.bamboo.module.chain.dal.mysql.tokens.TokensMapper;
import cc.bamboo.module.chain.dal.mysql.useridentities.UserIdentitiesMapper;
import cc.bamboo.module.chain.service.contract.ContractCallService;
import cc.bamboo.module.chain.service.contract.ContractLoaderService;
import cc.bamboo.module.chain.service.identity.dto.*;
import cc.bamboo.module.chain.service.trex.TrexDeployService;
import cc.bamboo.module.chain.util.AbiEncoderUtil;
import cc.bamboo.module.chain.util.Web3jUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 身份管理服务实现
 * 
 * @author Swolf
 */
@Service
@Slf4j
public class IdentityServiceImpl implements IdentityService {
    
    @Resource
    private Web3j web3j;
    
    @Resource
    private Web3jConfig web3jConfig;
    
    @Resource
    private ContractLoaderService contractLoaderService;
    
    @Resource
    private ContractCallService contractCallService;
    
    @Resource
    private TrexDeployService trexDeployService;
    
    @Resource
    private BlockchainAddressesMapper blockchainAddressesMapper;
    
    @Resource
    private ClaimIssuerIdentitiesMapper claimIssuerIdentitiesMapper;
    
    @Resource
    private UserIdentitiesMapper userIdentitiesMapper;
    
    @Resource
    private AddressIdentitiesMapper addressIdentitiesMapper;
    
    @Resource
    private TokensMapper tokensMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdentityOperationRespDTO addClaimIssuer(String address) {
        // 检查地址是否已存在
        ClaimIssuerIdentitiesDO existingIdentity = claimIssuerIdentitiesMapper.selectOne(
                ClaimIssuerIdentitiesDO::getAddress, address);
        
        if (existingIdentity != null) {
            // 完成待处理的操作
            return completePendingClaimIssuerOperations(existingIdentity.getId());
        }
        
        // 检查地址是否在 blockchain_addresses 表中
        BlockchainAddressesDO blockchainAddress = blockchainAddressesMapper.selectOne(
                BlockchainAddressesDO::getAddress, address);
        if (blockchainAddress == null) {
            throw new RuntimeException("地址 " + address + " 未在 blockchain_addresses 表中找到");
        }
        
        Credentials deployerCredentials = getDeployerCredentials();
        String managementKey = deployerCredentials.getAddress();
        
        // 创建 ClaimIssuer 记录
        ClaimIssuerIdentitiesDO identity = new ClaimIssuerIdentitiesDO();
        identity.setAddress(address);
        identity.setManagementKey(managementKey);
        identity.setBlockchainAddressId(blockchainAddress.getId());
        identity.setContractDeployed(false);
        identity.setClaimKeySetup(false);
        identity.setStatus(IdentityConstants.STATUS_ACTIVE);
        claimIssuerIdentitiesMapper.insert(identity);
        
        try {
            return completePendingClaimIssuerOperations(identity.getId());
        } catch (Exception e) {
            throw new RuntimeException("完成 ClaimIssuer 待处理操作失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdentityOperationRespDTO addUser(Long userId,String address, Long tokenId, Integer countryCode) {
        // 检查地址是否已存在
        UserIdentitiesDO existingIdentity = userIdentitiesMapper.selectOne(
                UserIdentitiesDO::getAddress, address);




        if (existingIdentity != null) {

            List<Long> associatedTokenIds = parseLongList(existingIdentity.getAssociatedTokenIds());
            //如果已经包含了该地址，直接返回
            if (associatedTokenIds.contains(tokenId)) {
                IdentityOperationRespDTO respDTO = new IdentityOperationRespDTO();
                respDTO.setId(existingIdentity.getId());
                respDTO.setTopicIds(parseLongList(existingIdentity.getTopicIds()));
                return respDTO;
            }

            if(!associatedTokenIds.isEmpty()){
                TokensDO tokensDO = tokensMapper.selectById(tokenId);
                //查询这些关联的tokenId
                List<TokensDO> tokensDOS = tokensMapper.selectList(
                        new LambdaQueryWrapper<TokensDO>()
                                .in(TokensDO::getId, associatedTokenIds));
                List<Long> identityRegistryStorageId = tokensDOS.stream()
                        .map(TokensDO::getIdentityRegistryStorageId)
                        .collect(Collectors.toList());
                //如果包含了该tokenId的identityRegistryStorageId，直接返回
                if (identityRegistryStorageId.contains(tokensDO.getIdentityRegistryStorageId())) {
                    IdentityOperationRespDTO respDTO = new IdentityOperationRespDTO();
                    respDTO.setId(existingIdentity.getId());
                    respDTO.setTopicIds(parseLongList(existingIdentity.getTopicIds()));
                    return respDTO;
                }
            }

            // 更新 countryCode 和 pendingTokenIds
            boolean needUpdate = false;
            if (countryCode != null) {
                existingIdentity.setCountryCode(countryCode.longValue());
                needUpdate = true;
            }
            if (tokenId != null) {
                List<Long> pendingTokenIds = parseLongList(existingIdentity.getPendingTokenIds());
                if (!pendingTokenIds.contains(tokenId)) {
                    pendingTokenIds.add(tokenId);
                    existingIdentity.setPendingTokenIds(JSONUtil.toJsonStr(pendingTokenIds));
                    needUpdate = true;
                }
            }
            if (needUpdate) {
                userIdentitiesMapper.updateById(existingIdentity);
            }
            
            return completePendingUserOperations(existingIdentity.getId());
        }
        
        // 检查地址是否在 blockchain_addresses 表中
       /* BlockchainAddressesDO blockchainAddress = blockchainAddressesMapper.selectOne(
                BlockchainAddressesDO::getAddress, address);
        if (blockchainAddress == null) {
            throw new RuntimeException("地址 " + address + " 未在 blockchain_addresses 表中找到");
        }*/
        
        Credentials deployerCredentials = getDeployerCredentials();
        String managementKey = deployerCredentials.getAddress();
        
        // 创建 User 记录
        UserIdentitiesDO identity = new UserIdentitiesDO();
        identity.setAddress(address);
        identity.setUserId(userId);
        identity.setManagementKey(managementKey);
        //identity.setBlockchainAddressId(blockchainAddress.getId());
        identity.setContractDeployed(false);
        identity.setClaimKeySetup(false);
        identity.setStatus(IdentityConstants.STATUS_ACTIVE);
        if (countryCode != null) {
            identity.setCountryCode(countryCode.longValue());
        }
        if (tokenId != null) {
            identity.setPendingTokenIds(JSONUtil.toJsonStr(Arrays.asList(tokenId)));
        }
        
        userIdentitiesMapper.insert(identity);
        
        try {
            return completePendingUserOperations(identity.getId());
        } catch (Exception e) {
            throw new RuntimeException("完成 User 待处理操作失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LinkWalletRespDTO linkWallet(String newWalletAddress, String oldWalletAddress) {
        // 从 AddressIdentity 表找出 oldWalletAddress 对应的 Identity
        AddressIdentitiesDO addressIdentity = addressIdentitiesMapper.selectOne(
                AddressIdentitiesDO::getAddress, oldWalletAddress,
                AddressIdentitiesDO::getType, IdentityConstants.IDENTITY_TYPE_USER);
        
        if (addressIdentity == null) {
            throw new RuntimeException("未找到地址 " + oldWalletAddress + " 对应的 Identity");
        }
        
        // 获取对应的 UserIdentity
        UserIdentitiesDO userIdentity = userIdentitiesMapper.selectById(addressIdentity.getIdentityId());
        if (userIdentity == null) {
            throw new RuntimeException("未找到 ID 为 " + addressIdentity.getIdentityId() + " 的 UserIdentity");
        }
        
        if (userIdentity.getContractAddress() == null) {
            throw new RuntimeException("UserIdentity 合约尚未部署");
        }
        
        Credentials deployerCredentials = getDeployerCredentials();
        
        LinkWalletRespDTO result = new LinkWalletRespDTO();
        result.setOldWalletAddress(oldWalletAddress);
        result.setNewWalletAddress(newWalletAddress);
        result.setIdentityAddress(userIdentity.getContractAddress());
        
        List<LinkWalletRespDTO.TokenRegistrationResult> registrationResults = new ArrayList<>();
        
        // 获取所有已部署的 Token，为新钱包注册 Identity
        List<TokensDO> tokens = tokensMapper.selectList(TokensDO::getStatus, IdentityConstants.STATUS_DEPLOYED);
        
        for (TokensDO token : tokens) {
            if (token.getIdentityRegistryAddress() == null) {
                continue;
            }
            
            LinkWalletRespDTO.TokenRegistrationResult tokenResult = new LinkWalletRespDTO.TokenRegistrationResult();
            tokenResult.setTokenId(token.getId());
            tokenResult.setTokenAddress(token.getAddress());
            
            try {
                // 调用 IdentityRegistry.registerIdentity() 方法
                int countryCode = userIdentity.getCountryCode() != null ? userIdentity.getCountryCode().intValue() : 0;
                
                log.info("为新钱包 {} 注册到 Token {} 的 IdentityRegistry", newWalletAddress, token.getId());
                
                TransactionReceipt receipt = contractCallService.registerIdentity(
                        deployerCredentials,
                        token.getIdentityRegistryAddress(),
                        newWalletAddress,
                        userIdentity.getContractAddress(),
                        countryCode
                );
                
                tokenResult.setTransactionHash(receipt.getTransactionHash());
                tokenResult.setBlockNumber(receipt.getBlockNumber().longValue());
                
                log.info("新钱包注册成功，txHash: {}", receipt.getTransactionHash());
                
            } catch (Exception e) {
                tokenResult.setError(e.getMessage());
            }
            
            registrationResults.add(tokenResult);
        }
        
        result.setRegistrationResults(registrationResults);
        
        // 写入 AddressIdentity 表
        AddressIdentitiesDO newAddressIdentity = new AddressIdentitiesDO();
        newAddressIdentity.setAddress(newWalletAddress);
        newAddressIdentity.setIdentityId(userIdentity.getId());
        newAddressIdentity.setType(IdentityConstants.IDENTITY_TYPE_USER);
        newAddressIdentity.setContractAddress(userIdentity.getContractAddress());
        addressIdentitiesMapper.insert(newAddressIdentity);
        
        return result;
    }

    @Override
    public LinkNewWalletRespDTO linkWallet(String newWalletAddress, Long userId) {
        LinkNewWalletRespDTO tokenResult  = new LinkNewWalletRespDTO();
        Credentials deployerCredentials = getDeployerCredentials();
        // 获取对应的 UserIdentity
        LambdaQueryWrapper<UserIdentitiesDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserIdentitiesDO::getUserId, userId);
        lambdaQueryWrapper.last("limit 1");
        UserIdentitiesDO userIdentity = userIdentitiesMapper.selectOne(lambdaQueryWrapper);
        if (userIdentity == null) {
            throw new RuntimeException("用户没有userIdentity："+userId);
        }
        //获取sign的Token
        TokensDO token =  tokensMapper.getSignToken();

        try {
            // 调用 IdentityRegistry.registerIdentity() 方法
            int countryCode = userIdentity.getCountryCode() != null ? userIdentity.getCountryCode().intValue() : 0;

            log.info("为新钱包 {} 注册到 Token {} 的 IdentityRegistry", newWalletAddress, token.getId());

            TransactionReceipt receipt = contractCallService.registerIdentity(
                    deployerCredentials,
                    token.getIdentityRegistryAddress(),
                    newWalletAddress,
                    userIdentity.getContractAddress(),
                    countryCode
            );

            tokenResult.setTransactionHash(receipt.getTransactionHash());


            log.info("新钱包注册成功，txHash: {}", receipt.getTransactionHash());

        } catch (Exception e) {
            tokenResult.setError(e.getMessage());
            tokenResult.setResult(1);
            throw new RuntimeException(e.getMessage());
        }
        return tokenResult;
    }

    @Override
    public DeleteWalletRespDTO deleteWallet(String walletAddress) {
        DeleteWalletRespDTO tokenResult  = new DeleteWalletRespDTO();
        Credentials deployerCredentials = getDeployerCredentials();

        //获取sign的Token
        TokensDO token =  tokensMapper.getSignToken();

        try {


            log.info("删除钱包:{}", walletAddress);

            TransactionReceipt receipt = contractCallService.deleteIdentity(
                    deployerCredentials,
                    token.getIdentityRegistryAddress(),
                    walletAddress
            );

            tokenResult.setTransactionHash(receipt.getTransactionHash());


            log.info("删除钱包成功，txHash: {}", receipt.getTransactionHash());

        } catch (Exception e) {
            tokenResult.setError(e.getMessage());
            tokenResult.setResult(1);
            throw new RuntimeException(e.getMessage());
        }
        return tokenResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IssueClaimRespDTO issueClaim(IssueClaimReqDTO reqDTO) {
        // 获取 ClaimIssuer
        ClaimIssuerIdentitiesDO claimIssuer = claimIssuerIdentitiesMapper.selectById(reqDTO.getClaimIssuerId());
        if (claimIssuer == null) {
            throw new RuntimeException("未找到 ClaimIssuer");
        }
        
        // 获取 User
        UserIdentitiesDO userIdentity = userIdentitiesMapper.selectById(reqDTO.getUserId());
        if (userIdentity == null) {
            throw new RuntimeException("未找到用户身份");
        }
        
        if (userIdentity.getContractAddress() == null) {
            throw new RuntimeException("用户 Identity 合约尚未部署");
        }
        
        Credentials issuerCredentials = getDeployerCredentials();
        
        // 计算数据哈希
        byte[] dataBytes = Numeric.hexStringToByteArray(reqDTO.getData());
        String dataHash = AbiEncoderUtil.hashAddressUint256Bytes(
                userIdentity.getContractAddress(),
                Numeric.toBigInt(reqDTO.getTopic()),
                dataBytes
        );
        
        // 签名
        byte[] dataHashBytes = Numeric.hexStringToByteArray(dataHash);
        String signature = Web3jUtil.signMessage(issuerCredentials, dataHashBytes);
        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        
        log.info("为用户 {} 签发 Claim，topic: {}", userIdentity.getAddress(), reqDTO.getTopic());
        
        try {
            // 调用 userIdentity.addClaim() 方法
            TransactionReceipt receipt = contractCallService.addClaim(
                    issuerCredentials,
                    userIdentity.getContractAddress(),
                    Numeric.toBigInt(reqDTO.getTopic()),
                    java.math.BigInteger.valueOf(reqDTO.getScheme()),
                    claimIssuer.getContractAddress(),
                    signatureBytes,
                    dataBytes,
                    reqDTO.getUri() != null ? reqDTO.getUri() : ""
            );
            
            log.info("Claim 签发成功，txHash: {}", receipt.getTransactionHash());
            
            IssueClaimRespDTO result = new IssueClaimRespDTO();
            result.setClaimIssuerId(reqDTO.getClaimIssuerId());
            result.setUserId(reqDTO.getUserId());
            result.setUserIdentityAddress(userIdentity.getContractAddress());
            result.setTopic(reqDTO.getTopic());
            result.setData(reqDTO.getData());
            result.setUri(reqDTO.getUri());
            result.setScheme(reqDTO.getScheme());
            result.setIssuerAddress(claimIssuer.getContractAddress());
            result.setIssuerWalletAddress(claimIssuer.getManagementKey());
            result.setTransactionHash(receipt.getTransactionHash());
            result.setBlockNumber(receipt.getBlockNumber().longValue());
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("签发 Claim 失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateUserIdentityTopicIds(Long userId, Long topicId) {
        UserIdentitiesDO identity = userIdentitiesMapper.selectById(userId);
        if (identity != null) {
            List<Long> topicIds = parseLongList(identity.getTopicIds());
            if (!topicIds.contains(topicId)) {
                topicIds.add(topicId);
            }
            identity.setTopicIds(JSONUtil.toJsonStr(topicIds));
            userIdentitiesMapper.updateById(identity);
        }
    }

    // ==================== 私有方法 ====================
    
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

    /**
     * 获取 deployer 凭证
     */
    private Credentials getClaimIssuerCredentials( String claimIssuerAddress) {
        BlockchainAddressesDO deployer = blockchainAddressesMapper.selectOne(
                BlockchainAddressesDO::getAddress, claimIssuerAddress);
        if (deployer == null) {
            throw new RuntimeException("未找到 ClaimIssuer 钱包，请先创建名为 '" + claimIssuerAddress + "' 的钱包");
        }
        return Credentials.create(deployer.getPrivateKey());
    }
    
    /**
     * 完成 ClaimIssuer 待处理操作
     */
    private IdentityOperationRespDTO completePendingClaimIssuerOperations(Long identityId) {
        ClaimIssuerIdentitiesDO identity = claimIssuerIdentitiesMapper.selectById(identityId);
        if (identity == null) {
            throw new RuntimeException("未找到 ClaimIssuerIdentity");
        }
        
        IdentityOperationRespDTO result = new IdentityOperationRespDTO();
        result.setId(identity.getId());
        result.setContractAddress(identity.getContractAddress());
        result.setManagementKey(identity.getManagementKey());
        result.setContractDeployed(identity.getContractDeployed());
        result.setClaimKeySetup(identity.getClaimKeySetup());
        
        List<String> operations = new ArrayList<>();
        
        // 部署合约
        if (!identity.getContractDeployed()) {
            try {
                String salt = "ClaimIssuer" + identity.getId();
                String contractAddress = deployIdentityContract(identity.getAddress(), salt);
                
                identity.setContractAddress(contractAddress);
                identity.setSalt(salt);
                identity.setContractDeployed(true);
                claimIssuerIdentitiesMapper.updateById(identity);
                
                result.setContractAddress(contractAddress);
                result.setContractDeployed(true);
                operations.add("合约已部署");
                
                // 写入 AddressIdentity 表
                saveAddressIdentity(identity.getAddress(), identity.getId(), 
                        IdentityConstants.IDENTITY_TYPE_CLAIM_ISSUER, contractAddress);
                
            } catch (Exception e) {
                throw new RuntimeException("部署合约失败: " + e.getMessage(), e);
            }
        } else {
            operations.add("合约已存在");
        }
        
        // 设置 ClaimKey
        if (!identity.getClaimKeySetup() && identity.getContractDeployed()) {
            try {
                setupClaimKey(identity.getContractAddress(), identity.getManagementKey());
                
                identity.setClaimKeySetup(true);
                claimIssuerIdentitiesMapper.updateById(identity);
                
                result.setClaimKeySetup(true);
                operations.add("ClaimKey 已设置");
                
            } catch (Exception e) {
                throw new RuntimeException("设置 ClaimKey 失败: " + e.getMessage(), e);
            }
        } else if (identity.getClaimKeySetup()) {
            operations.add("ClaimKey 已存在");
        }
        
        if (operations.isEmpty()) {
            operations.add("无待处理操作");
        }
        
        result.setOperations(operations);
        return result;
    }
    
    /**
     * 完成 User 待处理操作
     */
    private IdentityOperationRespDTO completePendingUserOperations(Long identityId) {
        UserIdentitiesDO identity = userIdentitiesMapper.selectById(identityId);
        if (identity == null) {
            throw new RuntimeException("未找到 UserIdentity");
        }
        
        IdentityOperationRespDTO result = new IdentityOperationRespDTO();
        result.setId(identity.getId());
        result.setContractAddress(identity.getContractAddress());
        result.setManagementKey(identity.getManagementKey());
        result.setContractDeployed(identity.getContractDeployed());
        result.setClaimKeySetup(identity.getClaimKeySetup());
        
        List<String> operations = new ArrayList<>();
        
        // 部署合约
        if (!identity.getContractDeployed()) {
            try {
                String salt = IdUtil.getSnowflakeNextIdStr();
                String contractAddress = deployIdentityContract(identity.getAddress(), salt);
                
                identity.setContractAddress(contractAddress);
                identity.setSalt(salt);
                identity.setContractDeployed(true);
                userIdentitiesMapper.updateById(identity);
                
                result.setContractAddress(contractAddress);
                result.setContractDeployed(true);
                operations.add("合约已部署");

                // 写入 AddressIdentity 表
                saveAddressIdentity(identity.getAddress(), identity.getId(),
                        IdentityConstants.IDENTITY_TYPE_USER, contractAddress);
                
            } catch (Exception e) {
                throw new RuntimeException("部署合约失败: " + e.getMessage(), e);
            }
        } else {
            operations.add("合约已存在");
        }
        
        // 设置 ClaimKey
        if (!identity.getClaimKeySetup() && identity.getContractDeployed()) {
            try {
                setupClaimKey(identity.getContractAddress(), identity.getManagementKey());
                
                identity.setClaimKeySetup(true);
                userIdentitiesMapper.updateById(identity);
                
                result.setClaimKeySetup(true);
                operations.add("ClaimKey 已设置");
                
            } catch (Exception e) {
                throw new RuntimeException("设置 ClaimKey 失败: " + e.getMessage(), e);
            }
        } else if (identity.getClaimKeySetup()) {
            operations.add("ClaimKey 已存在");
        }
        
        // 处理待关联的 Token
        List<Long> pendingTokenIds = parseLongList(identity.getPendingTokenIds());
        List<Long> associatedTokenIds = parseLongList(identity.getAssociatedTokenIds());
        
        if (!pendingTokenIds.isEmpty()) {
            for (Long tokenId : new ArrayList<>(pendingTokenIds)) {
                try {
                    TokensDO token = tokensMapper.selectById(tokenId);
                    if (token == null) {
                        operations.add("Token " + tokenId + " 不存在");
                        continue;
                    }
                    
                    if (token.getIdentityRegistryAddress() == null) {
                        operations.add("Token " + tokenId + " 没有 IdentityRegistry 地址");
                        continue;
                    }
                    
                    // 调用 IdentityRegistry.registerIdentity() 方法
                    Credentials deployerCredentials = getDeployerCredentials();
                    int countryCode = identity.getCountryCode() != null ? identity.getCountryCode().intValue() : 0;
                    
                    log.info("注册用户 {} 到 Token {} 的 IdentityRegistry", identity.getAddress(), tokenId);
                    
                    TransactionReceipt receipt = contractCallService.registerIdentity(
                            deployerCredentials,
                            token.getIdentityRegistryAddress(),
                            identity.getAddress(),
                            result.getContractAddress(),
                            countryCode
                    );
                    
                    log.info("用户注册成功，txHash: {}", receipt.getTransactionHash());
                    
                    associatedTokenIds.add(tokenId);
                    pendingTokenIds.remove(tokenId);
                    operations.add("已注册到 Token " + tokenId);
                    
                } catch (Exception e) {
                    operations.add("注册到 Token " + tokenId + " 失败: " + e.getMessage());
                }
            }
            
            identity.setAssociatedTokenIds(JSONUtil.toJsonStr(associatedTokenIds));
            identity.setPendingTokenIds(pendingTokenIds.isEmpty() ? null : JSONUtil.toJsonStr(pendingTokenIds));
            userIdentitiesMapper.updateById(identity);
        }
        
        if (operations.isEmpty()) {
            operations.add("无待处理操作");
        }
        
        result.setOperations(operations);
        return result;
    }
    
    /**
     * 部署 Identity 合约
     */
    private String deployIdentityContract(String walletAddress, String salt) throws Exception {
        // 获取 IdFactory 合约地址
        ContractDeploymentsDO idFactory = trexDeployService.getDeployedContract(ContractTypes.ONCHAIN_ID_ID_FACTORY);
        if (idFactory == null) {
            throw new RuntimeException("IdFactory 未部署");
        }
        
        Credentials deployerCredentials = getDeployerCredentials();
        
        // 计算 managementKey 的 keccak256 哈希
        String managementKeyHash = AbiEncoderUtil.hashAddress(deployerCredentials.getAddress());
        byte[] keyHashBytes = org.web3j.utils.Numeric.hexStringToByteArray(managementKeyHash);
        
        log.info("部署 Identity 合约，钱包地址: {}, salt: {}, managementKeyHash: {}", 
                walletAddress, salt, managementKeyHash);
        
        // 调用 IdFactory.createIdentityWithManagementKeys()
        TransactionReceipt receipt = contractCallService.createIdentityWithManagementKeys(
                deployerCredentials,
                idFactory.getDeploymentAddress(),
                walletAddress,
                salt,
                java.util.Collections.singletonList(keyHashBytes)
        );
        
        // 从 WalletLinked 事件中解析 Identity 地址
        String identityAddress = contractCallService.parseWalletLinkedEvent(receipt);
        
        log.info("Identity 合约部署成功，地址: {}, txHash: {}", identityAddress, receipt.getTransactionHash());
        
        return identityAddress;
    }
    
    /**
     * 设置 ClaimKey
     */
    private void setupClaimKey(String identityAddress, String managementKey) throws Exception {
        // 计算 keyHash
        String keyHashHex = AbiEncoderUtil.hashAddress(managementKey);
        byte[] keyHash = org.web3j.utils.Numeric.hexStringToByteArray(keyHashHex);
        
        Credentials deployerCredentials = getDeployerCredentials();
        
        log.info("设置 ClaimKey，Identity: {}, keyHash: {}", identityAddress, keyHashHex);
        
        // 调用 Identity.addKey()
        // KeyPurpose.CLAIM = 3, KeyType.ECDSA = 1
        TransactionReceipt receipt = contractCallService.addKey(
                deployerCredentials,
                identityAddress,
                keyHash,
                java.math.BigInteger.valueOf(IdentityConstants.KEY_PURPOSE_CLAIM),
                java.math.BigInteger.valueOf(IdentityConstants.KEY_TYPE_ECDSA)
        );
        
        log.info("ClaimKey 设置成功，txHash: {}", receipt.getTransactionHash());
    }
    
    /**
     * 保存地址身份映射
     */
    private void saveAddressIdentity(String address, Long identityId, String type, String contractAddress) {
        AddressIdentitiesDO existing = addressIdentitiesMapper.selectOne(
                AddressIdentitiesDO::getAddress, address,
                AddressIdentitiesDO::getType, type);
        
        if (existing != null) {
            existing.setContractAddress(contractAddress);
            addressIdentitiesMapper.updateById(existing);
        } else {
            AddressIdentitiesDO addressIdentity = new AddressIdentitiesDO();
            addressIdentity.setAddress(address);
            addressIdentity.setIdentityId(identityId);
            addressIdentity.setType(type);
            addressIdentity.setContractAddress(contractAddress);
            addressIdentitiesMapper.insert(addressIdentity);
        }
    }
    
    /**
     * 解析 Long 列表
     */
    private List<Long> parseLongList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return JSONUtil.toList(json, Long.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
