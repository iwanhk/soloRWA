package cc.bamboo.module.chain.constant;

/**
 * 合约类型常量
 * 
 * @author Swolf
 */
public interface ContractTypes {
    
    // ==================== OnChainId 合约 ====================
    
    /** Identity 实现合约 */
    String ONCHAIN_ID_IDENTITY = "OnChainId+Identity";
    /** Identity ImplementationAuthority */
    String ONCHAIN_ID_IMPLEMENTATION_AUTHORITY = "OnChainId+ImplementationAuthority";
    /** Identity Factory */
    String ONCHAIN_ID_ID_FACTORY = "OnChainId+IdFactory";
    /** ClaimIssuer 合约 */
    String ONCHAIN_ID_CLAIM_ISSUER = "OnChainId+ClaimIssuer";
    
    // ==================== ERC3643 实现合约 ====================
    
    /** Token 实现合约 */
    String ERC3643_TOKEN_IMPL = "ERC3643+TokenImpl";
    /** ClaimTopicsRegistry 实现合约 */
    String ERC3643_CLAIM_TOPICS_REGISTRY_IMPL = "ERC3643+ClaimTopicsRegistryImpl";
    /** TrustedIssuersRegistry 实现合约 */
    String ERC3643_TRUSTED_ISSUERS_REGISTRY_IMPL = "ERC3643+TrustedIssuersRegistryImpl";
    /** IdentityRegistryStorage 实现合约 */
    String ERC3643_IDENTITY_REGISTRY_STORAGE_IMPL = "ERC3643+IdentityRegistryStorageImpl";
    /** IdentityRegistry 实现合约 */
    String ERC3643_IDENTITY_REGISTRY_IMPL = "ERC3643+IdentityRegistryImpl";
    /** ModularCompliance 实现合约 */
    String ERC3643_MODULAR_COMPLIANCE_IMPL = "ERC3643+ModularComplianceImpl";
    
    // ==================== ERC3643 基础设施 ====================
    
    /** TREXImplementationAuthority */
    String ERC3643_IMPLEMENTATION_AUTHORITY = "ERC3643+ImplementationAuthority";
    /** TREXFactory */
    String ERC3643_FACTORY = "ERC3643+TREXFactory";
    /** IAFactory */
    String ERC3643_IA_FACTORY = "ERC3643+IAFactory";
}
