package cc.bamboo.module.chain.service.trex;

import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.module.chain.config.Web3jConfig;
import cc.bamboo.module.chain.constant.ContractTypes;
import cc.bamboo.module.chain.constant.IdentityConstants;
import cc.bamboo.module.chain.constant.InitSteps;
import cc.bamboo.module.chain.dal.dataobject.blockchainaddresses.BlockchainAddressesDO;
import cc.bamboo.module.chain.dal.dataobject.contractdeployments.ContractDeploymentsDO;
import cc.bamboo.module.chain.dal.dataobject.identityregistrystorages.IdentityRegistryStoragesDO;
import cc.bamboo.module.chain.dal.dataobject.systeminitialization.SystemInitializationDO;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.module.chain.dal.mysql.blockchainaddresses.BlockchainAddressesMapper;
import cc.bamboo.module.chain.dal.mysql.contractdeployments.ContractDeploymentsMapper;
import cc.bamboo.module.chain.dal.mysql.identityregistrystorages.IdentityRegistryStoragesMapper;
import cc.bamboo.module.chain.dal.mysql.systeminitialization.SystemInitializationMapper;
import cc.bamboo.module.chain.dal.mysql.tokens.TokensMapper;
import cc.bamboo.module.chain.dal.mysql.claimtopics.ClaimTopicsMapper;
import cc.bamboo.module.chain.dal.dataobject.claimtopics.ClaimTopicsDO;
import cc.bamboo.module.chain.service.contract.ContractCallService;
import cc.bamboo.module.chain.service.contract.ContractLoaderService;
import cc.bamboo.module.chain.service.trex.dto.DeployTokenReqDTO;
import cc.bamboo.module.chain.service.trex.dto.InitStatusRespDTO;
import cc.bamboo.module.chain.util.Web3jUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.*;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TREX 部署服务实现
 * 
 * @author Swolf
 */
@Service
@Slf4j
public class TrexDeployServiceImpl implements TrexDeployService {
    
    @Resource
    private Web3j web3j;
    
    @Resource
    private Web3jConfig web3jConfig;
    
    @Resource
    private ContractLoaderService contractLoaderService;
    
    @Resource
    private ContractCallService contractCallService;
    
    @Resource
    private BlockchainAddressesMapper blockchainAddressesMapper;
    
    @Resource
    private SystemInitializationMapper systemInitializationMapper;
    
    @Resource
    private ContractDeploymentsMapper contractDeploymentsMapper;
    
    @Resource
    private IdentityRegistryStoragesMapper identityRegistryStoragesMapper;
    
    @Resource
    private TokensMapper tokensMapper;
    
    @Resource
    private ClaimTopicsMapper claimTopicsMapper;
    
    /**
     * 所有初始化步骤
     */
    private static final String[] ALL_INIT_STEPS = {
            InitSteps.ONCHAIN_ID_IDENTITY,
            InitSteps.ONCHAIN_ID_IA,
            InitSteps.ONCHAIN_ID_FACTORY,
            InitSteps.ONCHAIN_ID_CLAIM_ISSUER,
            InitSteps.ERC3643_TOKEN_IMPL,
            InitSteps.ERC3643_CTR_IMPL,
            InitSteps.ERC3643_TIR_IMPL,
            InitSteps.ERC3643_IRS_IMPL,
            InitSteps.ERC3643_IR_IMPL,
            InitSteps.ERC3643_MC_IMPL,
            InitSteps.ERC3643_IA,
            InitSteps.ERC3643_IA_VERSION,
            InitSteps.ERC3643_FACTORY,
            InitSteps.ERC3643_IA_FACTORY,
            InitSteps.ERC3643_IA_CONFIG,
            InitSteps.FIRST_IRS
    };
    
    @Override
    public InitStatusRespDTO getInitializationStatus() {
        // 查询所有初始化步骤
        List<SystemInitializationDO> steps = systemInitializationMapper.selectList();
        
        // 查询所有已部署的合约
        List<ContractDeploymentsDO> contracts = contractDeploymentsMapper.selectList();
        
        // 查询 IRS 数量
        Long irsCount = identityRegistryStoragesMapper.selectCount();
        
        // 计算已完成的步骤数
        long completedSteps = steps.stream()
                .filter(s -> IdentityConstants.STATUS_COMPLETED.equals(s.getStatus()))
                .count();
        
        boolean isFullyInitialized = completedSteps == ALL_INIT_STEPS.length;
        
        InitStatusRespDTO result = new InitStatusRespDTO();
        result.setIsFullyInitialized(isFullyInitialized);
        result.setProgress(completedSteps + "/" + ALL_INIT_STEPS.length);
        result.setSteps(steps);
        result.setContracts(contracts);
        result.setIdentityRegistryStorageCount(irsCount);
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> initializeSystem(String claimIssuerManagementKey) {
        // 获取 deployer 钱包
        Credentials deployerCredentials = getDeployerCredentials();
        String deployerAddress = deployerCredentials.getAddress();
        String managementKey = claimIssuerManagementKey != null ? claimIssuerManagementKey : deployerAddress;
        
        Map<String, Object> results = new HashMap<>();
        
        try {
            // ========== 第一阶段：OnChainId 部署 ==========
            
            // Step 1: 部署 Identity 实现
            String identityImpl = executeStep(InitSteps.ONCHAIN_ID_IDENTITY, () -> {
                return deployContract("Identity", ContractTypes.ONCHAIN_ID_IDENTITY, 
                        deployerCredentials, Arrays.asList(new Address(deployerAddress), new Bool(true)));
            });
            results.put("identityImpl", identityImpl);
            
            // Step 2: 部署 Identity ImplementationAuthority
            String identityIA = executeStep(InitSteps.ONCHAIN_ID_IA, () -> {
                return deployContract("ImplementationAuthority", ContractTypes.ONCHAIN_ID_IMPLEMENTATION_AUTHORITY,
                        deployerCredentials, Arrays.asList(new Address(identityImpl)));
            });
            results.put("identityIA", identityIA);
            
            // Step 3: 部署 Identity Factory
            String identityFactory = executeStep(InitSteps.ONCHAIN_ID_FACTORY, () -> {
                return deployContract("IdFactory", ContractTypes.ONCHAIN_ID_ID_FACTORY,
                        deployerCredentials, Arrays.asList(new Address(identityIA)));
            });
            results.put("identityFactory", identityFactory);
            
            // Step 4: 部署 ClaimIssuer
            String claimIssuer = executeStep(InitSteps.ONCHAIN_ID_CLAIM_ISSUER, () -> {
                return deployContract("ClaimIssuer", ContractTypes.ONCHAIN_ID_CLAIM_ISSUER,
                        deployerCredentials, Arrays.asList(new Address(managementKey)));
            });
            results.put("claimIssuer", claimIssuer);
            
            // ========== 第二阶段：ERC3643 实现合约部署 ==========
            
            // Step 5: 部署 Token 实现
            String tokenImpl = executeStep(InitSteps.ERC3643_TOKEN_IMPL, () -> {
                return deployContract("Token", ContractTypes.ERC3643_TOKEN_IMPL,
                        deployerCredentials, Collections.emptyList());
            });
            results.put("tokenImpl", tokenImpl);
            
            // Step 6: 部署 ClaimTopicsRegistry 实现
            String ctrImpl = executeStep(InitSteps.ERC3643_CTR_IMPL, () -> {
                return deployContract("ClaimTopicsRegistry", ContractTypes.ERC3643_CLAIM_TOPICS_REGISTRY_IMPL,
                        deployerCredentials, Collections.emptyList());
            });
            results.put("ctrImpl", ctrImpl);
            
            // Step 7: 部署 TrustedIssuersRegistry 实现
            String tirImpl = executeStep(InitSteps.ERC3643_TIR_IMPL, () -> {
                return deployContract("TrustedIssuersRegistry", ContractTypes.ERC3643_TRUSTED_ISSUERS_REGISTRY_IMPL,
                        deployerCredentials, Collections.emptyList());
            });
            results.put("tirImpl", tirImpl);
            
            // Step 8: 部署 IdentityRegistryStorage 实现
            String irsImpl = executeStep(InitSteps.ERC3643_IRS_IMPL, () -> {
                return deployContract("IdentityRegistryStorage", ContractTypes.ERC3643_IDENTITY_REGISTRY_STORAGE_IMPL,
                        deployerCredentials, Collections.emptyList());
            });
            results.put("irsImpl", irsImpl);
            
            // Step 9: 部署 IdentityRegistry 实现
            String irImpl = executeStep(InitSteps.ERC3643_IR_IMPL, () -> {
                return deployContract("IdentityRegistry", ContractTypes.ERC3643_IDENTITY_REGISTRY_IMPL,
                        deployerCredentials, Collections.emptyList());
            });
            results.put("irImpl", irImpl);
            
            // Step 10: 部署 ModularCompliance 实现
            String mcImpl = executeStep(InitSteps.ERC3643_MC_IMPL, () -> {
                return deployContract("ModularCompliance", ContractTypes.ERC3643_MODULAR_COMPLIANCE_IMPL,
                        deployerCredentials, Collections.emptyList());
            });
            results.put("mcImpl", mcImpl);
            
            // ========== 第三阶段：TREX IA 和工厂部署 ==========
            
            // Step 11: 部署 TREXImplementationAuthority
            String trexIA = executeStep(InitSteps.ERC3643_IA, () -> {
                return deployContract("TREXImplementationAuthority", ContractTypes.ERC3643_IMPLEMENTATION_AUTHORITY,
                        deployerCredentials, Arrays.asList(
                                new Bool(true),
                                new Address("0x0000000000000000000000000000000000000000"),
                                new Address("0x0000000000000000000000000000000000000000")
                        ));
            });
            results.put("trexIA", trexIA);
            
            // Step 12: 添加版本到 TREX IA (需要调用合约方法)
            executeStep(InitSteps.ERC3643_IA_VERSION, () -> {
                // 调用 trexIA.addAndUseTREXVersion() 方法
                // 构建 Version 结构体: (uint16 major, uint16 minor, uint16 patch)
                // 构建 TREXContracts 结构体: 包含所有实现合约地址
                log.info("配置 TREX IA 版本，添加 v4.0.0");
                
                Credentials creds = getDeployerCredentials();
                
                // 构建函数调用
                org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                        "addAndUseTREXVersion",
                        Arrays.asList(
                                // Version 结构体 (major, minor, patch)
                                new org.web3j.abi.datatypes.DynamicStruct(
                                        new org.web3j.abi.datatypes.generated.Uint16(4),
                                        new org.web3j.abi.datatypes.generated.Uint16(0),
                                        new org.web3j.abi.datatypes.generated.Uint16(0)
                                ),
                                // TREXContracts 结构体
                                new org.web3j.abi.datatypes.DynamicStruct(
                                        new Address(tokenImpl),
                                        new Address(ctrImpl),
                                        new Address(tirImpl),
                                        new Address(irsImpl),
                                        new Address(irImpl),
                                        new Address(mcImpl)
                                )
                        ),
                        Collections.emptyList()
                );
                
                TransactionReceipt receipt = contractCallService.callContract(creds, trexIA, function);
                log.info("TREX IA 版本配置成功，txHash: {}", receipt.getTransactionHash());
                
                return trexIA;
            });
            
            // Step 13: 部署 TREXFactory
            String trexFactory = executeStep(InitSteps.ERC3643_FACTORY, () -> {
                return deployContract("TREXFactory", ContractTypes.ERC3643_FACTORY,
                        deployerCredentials, Arrays.asList(
                                new Address(trexIA),
                                new Address(identityFactory)
                        ));
            });
            results.put("trexFactory", trexFactory);
            
            // Step 14: 部署 IAFactory
            String iaFactory = executeStep(InitSteps.ERC3643_IA_FACTORY, () -> {
                return deployContract("IAFactory", ContractTypes.ERC3643_IA_FACTORY,
                        deployerCredentials, Arrays.asList(new Address(trexFactory)));
            });
            results.put("iaFactory", iaFactory);
            
            // Step 15: 配置 TREX IA
            executeStep(InitSteps.ERC3643_IA_CONFIG, () -> {
                // 调用 trexIA.setTREXFactory() 和 trexIA.setIAFactory()
                log.info("配置 TREX IA，设置 TREXFactory 和 IAFactory");
                
                Credentials creds = getDeployerCredentials();
                
                // 调用 setTREXFactory
                org.web3j.abi.datatypes.Function setFactoryFunc = new org.web3j.abi.datatypes.Function(
                        "setTREXFactory",
                        Collections.singletonList(new Address(trexFactory)),
                        Collections.emptyList()
                );
                TransactionReceipt receipt1 = contractCallService.callContract(creds, trexIA, setFactoryFunc);
                log.info("setTREXFactory 成功，txHash: {}", receipt1.getTransactionHash());
                
                // 调用 setIAFactory
                org.web3j.abi.datatypes.Function setIAFactoryFunc = new org.web3j.abi.datatypes.Function(
                        "setIAFactory",
                        Collections.singletonList(new Address(iaFactory)),
                        Collections.emptyList()
                );
                TransactionReceipt receipt2 = contractCallService.callContract(creds, trexIA, setIAFactoryFunc);
                log.info("setIAFactory 成功，txHash: {}", receipt2.getTransactionHash());
                
                return trexIA;
            });
            
            // Step 16: 部署首个 IdentityRegistryStorage
            String firstIRS = executeStep(InitSteps.FIRST_IRS, () -> {
                return deployIdentityRegistryStorageInternal(deployerCredentials, trexIA, trexFactory);
            });
            results.put("firstIRS", firstIRS);
            
            results.put("success", true);
            results.put("message", "系统初始化成功");
            
        } catch (Exception e) {
            log.error("系统初始化失败", e);
            results.put("success", false);
            results.put("message", "系统初始化失败: " + e.getMessage());
            throw new RuntimeException("系统初始化失败", e);
        }
        
        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokensDO deployToken(DeployTokenReqDTO reqDTO) {

        InitStatusRespDTO status = getInitializationStatus();
        if (!status.getIsFullyInitialized()) {
            throw new RuntimeException("系统未完全初始化，请先调用 initializeSystem()");
        }

        IdentityRegistryStoragesDO irs = identityRegistryStoragesMapper.selectOne(
                new LambdaQueryWrapperX<IdentityRegistryStoragesDO>()
                        .lt(IdentityRegistryStoragesDO::getBoundTokenCount, IdentityConstants.MAX_TOKENS_PER_IRS)
                        .orderByAsc(IdentityRegistryStoragesDO::getBoundTokenCount)
                        .last("LIMIT 1")
        );
        if (irs == null) {
            throw new RuntimeException("没有可用的 IdentityRegistryStorage");
        }

        Credentials deployerCredentials = getDeployerCredentials();

        // 从数据库读取所有 ClaimTopics
        List<ClaimTopicsDO> claimTopicsList = claimTopicsMapper.selectList();
        if (claimTopicsList == null || claimTopicsList.isEmpty()) {
            throw new RuntimeException("没有可用的 ClaimTopics，请先创建 ClaimTopics");
        }
        
        // 构建 claimTopics 数组（使用 topic 字段，即 uint256 哈希值）
        List<String> claimTopics = claimTopicsList.stream()
                .map(ClaimTopicsDO::getTopic)
                .collect(Collectors.toList());
        
        // 从请求中获取 issuers（ClaimIssuer 地址列表）
        List<String> issuers = reqDTO.getIssuers();
        if (issuers == null || issuers.isEmpty()) {
            throw new RuntimeException("必须提供至少一个 ClaimIssuer 地址");
        }
        
        // 构建 issuerClaims：每个 issuer 对应所有的 claimTopics
        List<List<String>> issuerClaims = new ArrayList<>();
        for (int i = 0; i < issuers.size(); i++) {
            // 每个 issuer 可以签发所有的 claimTopics
            issuerClaims.add(new ArrayList<>(claimTopics));
        }

        TokensDO token = new TokensDO();
        token.setName(reqDTO.getName());
        token.setSymbol(reqDTO.getSymbol());
        token.setDecimals(reqDTO.getDecimals() != null ? reqDTO.getDecimals().longValue() : 18L);
        token.setOwnerAddress(reqDTO.getOwnerAddress());
        token.setDeployerAddress(deployerCredentials.getAddress());
        token.setIdentityRegistryStorageId(irs.getId());
        token.setSalt(reqDTO.getSalt());
        token.setTokenAgents(JSONUtil.toJsonStr(reqDTO.getTokenAgents()));
        token.setClaimTopics(JSONUtil.toJsonStr(claimTopics));
        token.setIssuers(JSONUtil.toJsonStr(issuers));
        token.setIssuerClaims(JSONUtil.toJsonStr(issuerClaims));
        token.setStatus(IdentityConstants.STATUS_PENDING);
        token.setProjectId(reqDTO.getProjectId());
        tokensMapper.insert(token);

        try {
            ContractDeploymentsDO trexFactory = getDeployedContract(ContractTypes.ERC3643_FACTORY);
            if (trexFactory == null) {
                throw new RuntimeException("TREXFactory 未部署");
            }

            log.info("开始部署 Token: {}", reqDTO.getName());
            
            // 使用手动 ABI 编码来处理 uint256[][] 类型
            String encodedData = encodeDeployTREXSuiteData(
                    reqDTO, 
                    irs.getAddress(), 
                    deployerCredentials.getAddress(),
                    claimTopics,
                    issuers,
                    issuerClaims
            );
            
            // 获取 nonce
            BigInteger nonce = Web3jUtil.getLatestNonce(web3j, deployerCredentials.getAddress());
            
            // 创建交易
            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    BigInteger.valueOf(web3jConfig.getGasPrice()),
                    BigInteger.valueOf(web3jConfig.getGasLimit()),
                    trexFactory.getDeploymentAddress(),
                    encodedData
            );
            
            // 发送交易
            String txHash = Web3jUtil.sendRawTransaction(web3j, deployerCredentials, rawTransaction, web3jConfig.getChainId());
            log.info("部署 Token 交易已发送，txHash: {}", txHash);
            
            // 等待交易确认
            TransactionReceipt receipt = Web3jUtil.waitForReceipt(web3j, txHash);
            log.info("部署 Token 交易已确认，receipt: {}", receipt);

            String[] addresses = contractCallService.parseTREXSuiteDeployedEvent(receipt);

            token.setAddress(addresses[0]);
            token.setIdentityRegistryAddress(addresses[1]);
            token.setTrustedIssuersRegistryAddress(addresses[3]);
            token.setClaimTopicsRegistryAddress(addresses[4]);
            token.setModularComplianceAddress(addresses[5]);
            token.setTransactionHash(receipt.getTransactionHash());
            token.setBlockNumber(receipt.getBlockNumber().longValue());
            token.setStatus(IdentityConstants.STATUS_DEPLOYED);
            tokensMapper.updateById(token);

            irs.setBoundTokenCount(irs.getBoundTokenCount() + 1);
            irs.setStatus(IdentityConstants.STATUS_ACTIVE);
            identityRegistryStoragesMapper.updateById(irs);

            return token;

        } catch (Exception e) {
            token.setStatus(IdentityConstants.STATUS_FAILED);
            token.setErrorMessage(e.getMessage());
            tokensMapper.updateById(token);
            throw new RuntimeException("部署 Token 失败: " + e.getMessage(), e);
        }
    }


    /**
     * 手动编码 deployTREXSuite 函数调用数据
     * 因为 Web3j 4.10.x 不支持 uint256[][] 类型，需要手动编码
     */
    private String encodeDeployTREXSuiteData(
            DeployTokenReqDTO reqDTO, 
            String irsAddress, 
            String deployerAddress,
            List<String> claimTopics,
            List<String> issuers,
            List<List<String>> issuerClaims) {
        // 函数选择器: keccak256("deployTREXSuite(string,(address,string,string,uint8,address,address,address[],address[],address[],bytes[]),(uint256[],address[],uint256[][]))")
        // 前 4 字节
        String functionSelector = "0xcf753d37";  // 这个需要根据实际 ABI 计算
        
        StringBuilder encoded = new StringBuilder(functionSelector);
        
        // 零地址
        String zeroAddress = "0x0000000000000000000000000000000000000000";
        
        // tokenAgents: 使用请求中的值，如果为空则使用 deployer 地址
        List<String> tokenAgents = (reqDTO.getTokenAgents() != null && !reqDTO.getTokenAgents().isEmpty()) ?
                reqDTO.getTokenAgents() : Collections.singletonList(deployerAddress);
        
        try {
            // 参数偏移量（3个参数：string, tuple, tuple）
            // 第一个参数 salt (string) 的偏移量
            int offset1 = 3 * 32; // 3个参数的偏移量位置
            
            // 编码 salt (string)
            byte[] saltBytes = reqDTO.getSalt().getBytes();
            String saltEncoded = Numeric.toHexStringNoPrefix(
                    Numeric.toBytesPadded(BigInteger.valueOf(saltBytes.length), 32)
            ) + Numeric.toHexStringNoPrefix(saltBytes);
            // 补齐到 32 字节的倍数
            int saltPadding = (32 - (saltBytes.length % 32)) % 32;
            saltEncoded += repeatString("0", saltPadding * 2);
            
            // 第二个参数 TokenDetails (tuple) 的偏移量
            int offset2 = offset1 + saltEncoded.length() / 2;
            
            // 编码 TokenDetails
            String tokenDetailsEncoded = encodeTokenDetails(
                    reqDTO.getOwnerAddress(),
                    reqDTO.getName(),
                    reqDTO.getSymbol(),
                    reqDTO.getDecimals() != null ? reqDTO.getDecimals() : 18,
                    irsAddress,
                    zeroAddress,
                    Collections.singletonList(deployerAddress),  // irAgents
                    tokenAgents,
                    Collections.emptyList(),  // complianceModules
                    Collections.emptyList()   // complianceSettings
            );
            
            // 第三个参数 ClaimDetails (tuple) 的偏移量
            int offset3 = offset2 + tokenDetailsEncoded.length() / 2;
            
            // 编码 ClaimDetails（包含 uint256[][]）
            String claimDetailsEncoded = encodeClaimDetails(claimTopics, issuers, issuerClaims);
            
            // 组装最终数据
            encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(offset1), 32)));
            encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(offset2), 32)));
            encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(offset3), 32)));
            encoded.append(saltEncoded);
            encoded.append(tokenDetailsEncoded);
            encoded.append(claimDetailsEncoded);
            
            return encoded.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("编码 deployTREXSuite 数据失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * Java 8 兼容的字符串重复方法
     */
    private String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    /**
     * 编码 TokenDetails 结构体
     */
    private String encodeTokenDetails(String owner, String name, String symbol, int decimals,
                                      String irs, String onchainId, List<String> irAgents,
                                      List<String> tokenAgents, List<String> complianceModules,
                                      List<byte[]> complianceSettings) {
        StringBuilder encoded = new StringBuilder();
        
        // owner (address) - 固定 32 字节
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(Numeric.toBigInt(owner), 32)));
        
        // 动态类型的偏移量（name, symbol, irAgents, tokenAgents, complianceModules, complianceSettings）
        int baseOffset = 10 * 32; // 10个字段的位置
        
        // name offset
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(baseOffset), 32)));
        
        // 编码 name
        byte[] nameBytes = name.getBytes();
        String nameEncoded = Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(nameBytes.length), 32))
                + Numeric.toHexStringNoPrefix(nameBytes);
        int namePadding = (32 - (nameBytes.length % 32)) % 32;
        nameEncoded += repeatString("0", namePadding * 2);
        
        int symbolOffset = baseOffset + nameEncoded.length() / 2;
        
        // symbol offset
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(symbolOffset), 32)));
        
        // 编码 symbol
        byte[] symbolBytes = symbol.getBytes();
        String symbolEncoded = Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(symbolBytes.length), 32))
                + Numeric.toHexStringNoPrefix(symbolBytes);
        int symbolPadding = (32 - (symbolBytes.length % 32)) % 32;
        symbolEncoded += repeatString("0", symbolPadding * 2);
        
        // decimals (uint8) - 固定 32 字节
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(decimals), 32)));
        
        // irs (address) - 固定 32 字节
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(Numeric.toBigInt(irs), 32)));
        
        // ONCHAINID (address) - 固定 32 字节
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(Numeric.toBigInt(onchainId), 32)));
        
        // 计算剩余动态数组的偏移量
        int irAgentsOffset = symbolOffset + symbolEncoded.length() / 2;
        int tokenAgentsOffset = irAgentsOffset + encodeAddressArray(irAgents).length() / 2;
        int complianceModulesOffset = tokenAgentsOffset + encodeAddressArray(tokenAgents).length() / 2;
        int complianceSettingsOffset = complianceModulesOffset + encodeAddressArray(complianceModules).length() / 2;
        
        // irAgents offset
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(irAgentsOffset), 32)));
        
        // tokenAgents offset
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(tokenAgentsOffset), 32)));
        
        // complianceModules offset
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(complianceModulesOffset), 32)));
        
        // complianceSettings offset
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(complianceSettingsOffset), 32)));
        
        // 编码动态数据
        encoded.append(nameEncoded);
        encoded.append(symbolEncoded);
        encoded.append(encodeAddressArray(irAgents));
        encoded.append(encodeAddressArray(tokenAgents));
        encoded.append(encodeAddressArray(complianceModules));
        encoded.append(encodeBytesArray(complianceSettings));
        
        return encoded.toString();
    }
    
    /**
     * 编码 ClaimDetails 结构体（包含 uint256[][]）
     */
    private String encodeClaimDetails(List<String> claimTopics, List<String> issuers, List<List<String>> issuerClaims) {
        StringBuilder encoded = new StringBuilder();
        
        // 三个动态数组的偏移量
        int baseOffset = 3 * 32;
        
        // claimTopics offset
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(baseOffset), 32)));
        
        // 编码 claimTopics
        String claimTopicsEncoded = encodeUint256Array(claimTopics);
        int issuersOffset = baseOffset + claimTopicsEncoded.length() / 2;
        
        // issuers offset
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(issuersOffset), 32)));
        
        // 编码 issuers
        String issuersEncoded = encodeAddressArray(issuers);
        int issuerClaimsOffset = issuersOffset + issuersEncoded.length() / 2;
        
        // issuerClaims offset
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(issuerClaimsOffset), 32)));
        
        // 编码 issuerClaims (uint256[][])
        String issuerClaimsEncoded = encodeUint256ArrayArray(issuerClaims);
        
        // 组装数据
        encoded.append(claimTopicsEncoded);
        encoded.append(issuersEncoded);
        encoded.append(issuerClaimsEncoded);
        
        return encoded.toString();
    }
    
    /**
     * 编码 address[] 数组
     */
    private String encodeAddressArray(List<String> addresses) {
        StringBuilder encoded = new StringBuilder();
        
        // 数组长度
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(addresses.size()), 32)));
        
        // 数组元素
        for (String addr : addresses) {
            encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(Numeric.toBigInt(addr), 32)));
        }
        
        return encoded.toString();
    }
    
    /**
     * 编码 uint256[] 数组
     */
    private String encodeUint256Array(List<String> values) {
        StringBuilder encoded = new StringBuilder();
        
        // 数组长度
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(values.size()), 32)));
        
        // 数组元素
        for (String val : values) {
            encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(Numeric.toBigInt(val), 32)));
        }
        
        return encoded.toString();
    }
    
    /**
     * 编码 uint256[][] 二维数组（核心方法）
     */
    private String encodeUint256ArrayArray(List<List<String>> arrays) {
        StringBuilder encoded = new StringBuilder();
        
        // 外层数组长度
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(arrays.size()), 32)));
        
        // 计算每个内层数组的偏移量
        int baseOffset = arrays.size() * 32; // 偏移量位置
        List<String> innerArraysEncoded = new ArrayList<>();
        
        for (List<String> innerArray : arrays) {
            innerArraysEncoded.add(encodeUint256Array(innerArray));
        }
        
        // 写入偏移量
        int currentOffset = baseOffset;
        for (String innerEncoded : innerArraysEncoded) {
            encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(currentOffset), 32)));
            currentOffset += innerEncoded.length() / 2;
        }
        
        // 写入内层数组数据
        for (String innerEncoded : innerArraysEncoded) {
            encoded.append(innerEncoded);
        }
        
        return encoded.toString();
    }
    
    /**
     * 编码 bytes[] 数组
     */
    private String encodeBytesArray(List<byte[]> bytesArray) {
        StringBuilder encoded = new StringBuilder();
        
        // 数组长度
        encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(bytesArray.size()), 32)));
        
        if (bytesArray.isEmpty()) {
            return encoded.toString();
        }
        
        // 计算每个 bytes 的偏移量
        int baseOffset = bytesArray.size() * 32;
        List<String> bytesEncoded = new ArrayList<>();
        
        for (byte[] bytes : bytesArray) {
            String encoded_bytes = Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(bytes.length), 32))
                    + Numeric.toHexStringNoPrefix(bytes);
            int padding = (32 - (bytes.length % 32)) % 32;
            encoded_bytes += repeatString("0", padding * 2);
            bytesEncoded.add(encoded_bytes);
        }
        
        // 写入偏移量
        int currentOffset = baseOffset;
        for (String bytesEnc : bytesEncoded) {
            encoded.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(currentOffset), 32)));
            currentOffset += bytesEnc.length() / 2;
        }
        
        // 写入 bytes 数据
        for (String bytesEnc : bytesEncoded) {
            encoded.append(bytesEnc);
        }
        
        return encoded.toString();
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdentityRegistryStoragesDO deployIdentityRegistryStorage() {
        // 检查 TREX IA 是否已部署
        ContractDeploymentsDO trexIA = getDeployedContract(ContractTypes.ERC3643_IMPLEMENTATION_AUTHORITY);
        if (trexIA == null) {
            throw new RuntimeException("系统未初始化，请先调用 initializeSystem()");
        }
        
        ContractDeploymentsDO trexFactory = getDeployedContract(ContractTypes.ERC3643_FACTORY);
        
        Credentials deployerCredentials = getDeployerCredentials();
        String address;
        try {
            address = deployIdentityRegistryStorageInternal(
                    deployerCredentials, 
                    trexIA.getDeploymentAddress(),
                    trexFactory != null ? trexFactory.getDeploymentAddress() : null
            );
        } catch (Exception e) {
            throw new RuntimeException("部署 IdentityRegistryStorage 失败: " + e.getMessage(), e);
        }
        
        return identityRegistryStoragesMapper.selectOne(IdentityRegistryStoragesDO::getAddress, address);
    }
    
    @Override
    public ContractDeploymentsDO getDeployedContract(String contractType) {
        return contractDeploymentsMapper.selectOne(ContractDeploymentsDO::getContractType, contractType);
    }
    
    @Override
    public boolean isContractDeployed(String contractType) {
        return getDeployedContract(contractType) != null;
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
     * 检查步骤是否已完成
     */
    private boolean isStepCompleted(String step) {
        SystemInitializationDO record = systemInitializationMapper.selectOne(
                SystemInitializationDO::getStep, step);
        return record != null && IdentityConstants.STATUS_COMPLETED.equals(record.getStatus());
    }
    
    /**
     * 获取步骤的合约地址
     */
    private String getStepContractAddress(String step) {
        SystemInitializationDO record = systemInitializationMapper.selectOne(
                SystemInitializationDO::getStep, step);
        return record != null ? record.getContractAddress() : null;
    }
    
    /**
     * 更新初始化步骤状态
     */
    private void updateInitStep(String step, String status, String contractAddress, 
            String transactionHash, Long blockNumber, String errorMessage, String metadata) {
        SystemInitializationDO record = systemInitializationMapper.selectOne(
                SystemInitializationDO::getStep, step);
        
        if (record == null) {
            record = new SystemInitializationDO();
            record.setStep(step);
            record.setStatus(status);
            record.setContractAddress(contractAddress);
            record.setTransactionHash(transactionHash);
            record.setBlockNumber(blockNumber);
            record.setErrorMessage(errorMessage);
            record.setMetadata(metadata);
            systemInitializationMapper.insert(record);
        } else {
            record.setStatus(status);
            if (contractAddress != null) record.setContractAddress(contractAddress);
            if (transactionHash != null) record.setTransactionHash(transactionHash);
            if (blockNumber != null) record.setBlockNumber(blockNumber);
            if (errorMessage != null) record.setErrorMessage(errorMessage);
            if (metadata != null) record.setMetadata(metadata);
            systemInitializationMapper.updateById(record);
        }
    }
    
    /**
     * 执行初始化步骤
     */
    private String executeStep(String step, StepExecutor executor) throws Exception {
        // 检查是否已完成
        if (isStepCompleted(step)) {
            String address = getStepContractAddress(step);
            log.info("步骤 {} 已完成，合约地址: {}", step, address);
            return address;
        }
        
        // 更新状态为进行中
        updateInitStep(step, IdentityConstants.STATUS_IN_PROGRESS, null, null, null, null, null);
        
        try {
            String result = executor.execute();
            // 更新状态为已完成
            updateInitStep(step, IdentityConstants.STATUS_COMPLETED, result, null, null, null, null);
            log.info("步骤 {} 完成，结果: {}", step, result);
            return result;
        } catch (Exception e) {
            // 更新状态为失败
            updateInitStep(step, IdentityConstants.STATUS_FAILED, null, null, null, e.getMessage(), null);
            throw e;
        }
    }
    
    /**
     * 部署合约
     */
    @SuppressWarnings("rawtypes")
    private String deployContract(String contractName, String contractType, 
            Credentials credentials, List<Type> constructorParams) throws Exception {
        
        // 加载合约 bytecode
        String bytecode = contractLoaderService.loadBytecode(contractName);
        
        // 编码构造函数参数
        String encodedConstructor = FunctionEncoder.encodeConstructor(constructorParams);
        String data = bytecode + encodedConstructor.substring(2); // 去掉 0x 前缀
        
        // 获取 nonce
        BigInteger nonce = Web3jUtil.getLatestNonce(web3j, credentials.getAddress());
        
        // 创建交易
        RawTransaction rawTransaction = Web3jUtil.createContractTransaction(
                nonce,
                BigInteger.valueOf(web3jConfig.getGasPrice()),
                BigInteger.valueOf(web3jConfig.getGasLimit()),
                data
        );
        
        // 发送交易（使用带 chainId 的签名方式）
        String txHash = Web3jUtil.sendRawTransaction(web3j, credentials, rawTransaction, web3jConfig.getChainId());
        log.info("部署合约 {} 交易已发送，txHash: {}", contractName, txHash);
        
        // 等待交易确认
        TransactionReceipt receipt = Web3jUtil.waitForReceipt(web3j, txHash);
        String contractAddress = receipt.getContractAddress();
        log.info("合约 {} 部署成功，地址: {}", contractName, contractAddress);
        
        // 保存到数据库
        ContractDeploymentsDO deployment = new ContractDeploymentsDO();
        deployment.setContractType(contractType);
        deployment.setDeploymentAddress(contractAddress);
        deployment.setDeployerAddress(credentials.getAddress());
        deployment.setTransactionHash(txHash);
        deployment.setBlockNumber(receipt.getBlockNumber().longValue());
        deployment.setStatus(IdentityConstants.STATUS_DEPLOYED);
        contractDeploymentsMapper.insert(deployment);
        
        return contractAddress;
    }
    
    /**
     * 部署 IdentityRegistryStorage
     */
    private String deployIdentityRegistryStorageInternal(Credentials credentials, 
            String trexIAAddress, String trexFactoryAddress) throws Exception {
        
        // 部署 IdentityRegistryStorageProxy
        String bytecode = contractLoaderService.loadBytecode("IdentityRegistryStorageProxy");
        String encodedConstructor = FunctionEncoder.encodeConstructor(
                Arrays.asList(new Address(trexIAAddress)));
        String data = bytecode + encodedConstructor.substring(2);
        
        BigInteger nonce = Web3jUtil.getLatestNonce(web3j, credentials.getAddress());
        RawTransaction rawTransaction = Web3jUtil.createContractTransaction(
                nonce,
                BigInteger.valueOf(web3jConfig.getGasPrice()),
                BigInteger.valueOf(web3jConfig.getGasLimit()),
                data
        );
        
        String txHash = Web3jUtil.sendRawTransaction(web3j, credentials, rawTransaction, web3jConfig.getChainId());
        TransactionReceipt receipt = Web3jUtil.waitForReceipt(web3j, txHash);
        String address = receipt.getContractAddress();
        
        log.info("IdentityRegistryStorage 部署成功，地址: {}", address);
        
        // 如果有 TREXFactory，转移 IRS 的 owner 到 TREXFactory
        if (trexFactoryAddress != null) {
            try {
                log.info("转移 IRS 所有权到 TREXFactory: {}", trexFactoryAddress);
                TransactionReceipt transferReceipt = contractCallService.transferOwnership(
                        credentials, address, trexFactoryAddress);
                log.info("IRS 所有权转移成功，txHash: {}", transferReceipt.getTransactionHash());
            } catch (Exception e) {
                log.warn("转移 IRS 所有权失败: {}", e.getMessage());
            }
        }
        
        // 保存到数据库
        IdentityRegistryStoragesDO irs = new IdentityRegistryStoragesDO();
        irs.setAddress(address);
        irs.setDeployerAddress(credentials.getAddress());
        irs.setTransactionHash(txHash);
        irs.setBlockNumber(receipt.getBlockNumber().longValue());
        irs.setBoundTokenCount(0L);
        irs.setStatus(IdentityConstants.STATUS_DEPLOYED);
        identityRegistryStoragesMapper.insert(irs);
        
        return address;
    }
    
    /**
     * 步骤执行器接口
     */
    @FunctionalInterface
    private interface StepExecutor {
        String execute() throws Exception;
    }
}
