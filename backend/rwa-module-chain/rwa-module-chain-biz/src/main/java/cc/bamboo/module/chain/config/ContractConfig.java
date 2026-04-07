package cc.bamboo.module.chain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 合约配置类
 * 存储合约的 ABI 和 Bytecode 文件路径
 * 
 * @author Swolf
 */
@Configuration
@ConfigurationProperties(prefix = "contract")
@Data
public class ContractConfig {

    /**
     * 合约 ABI 文件目录
     */
    private String abiPath = "classpath:contracts/abi/";

    /**
     * 合约 Bytecode 文件目录
     */
    private String bytecodePath = "classpath:contracts/bytecode/";

    // ==================== OnChainId 合约 ====================

    /**
     * Identity 合约 ABI 文件名
     */
    private String identityAbi = "Identity.json";

    /**
     * ImplementationAuthority 合约 ABI 文件名
     */
    private String implementationAuthorityAbi = "ImplementationAuthority.json";

    /**
     * IdFactory 合约 ABI 文件名
     */
    private String idFactoryAbi = "IdFactory.json";

    /**
     * ClaimIssuer 合约 ABI 文件名
     */
    private String claimIssuerAbi = "ClaimIssuer.json";

    // ==================== ERC3643 合约 ====================

    /**
     * Token 合约 ABI 文件名
     */
    private String tokenAbi = "Token.json";

    /**
     * ClaimTopicsRegistry 合约 ABI 文件名
     */
    private String claimTopicsRegistryAbi = "ClaimTopicsRegistry.json";

    /**
     * TrustedIssuersRegistry 合约 ABI 文件名
     */
    private String trustedIssuersRegistryAbi = "TrustedIssuersRegistry.json";

    /**
     * IdentityRegistryStorage 合约 ABI 文件名
     */
    private String identityRegistryStorageAbi = "IdentityRegistryStorage.json";

    /**
     * IdentityRegistry 合约 ABI 文件名
     */
    private String identityRegistryAbi = "IdentityRegistry.json";

    /**
     * ModularCompliance 合约 ABI 文件名
     */
    private String modularComplianceAbi = "ModularCompliance.json";

    /**
     * TREXImplementationAuthority 合约 ABI 文件名
     */
    private String trexImplementationAuthorityAbi = "TREXImplementationAuthority.json";

    /**
     * TREXFactory 合约 ABI 文件名
     */
    private String trexFactoryAbi = "TREXFactory.json";

    /**
     * IAFactory 合约 ABI 文件名
     */
    private String iaFactoryAbi = "IAFactory.json";

    /**
     * IdentityRegistryStorageProxy 合约 ABI 文件名
     */
    private String identityRegistryStorageProxyAbi = "IdentityRegistryStorageProxy.json";

    /**
     * DividendRecord 合约地址
     */
    private String dividendRecordAddress;
}
