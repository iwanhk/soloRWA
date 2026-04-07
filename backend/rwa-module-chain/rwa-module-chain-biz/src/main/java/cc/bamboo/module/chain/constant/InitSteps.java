package cc.bamboo.module.chain.constant;

/**
 * 系统初始化步骤常量
 * 
 * @author Swolf
 */
public interface InitSteps {
    
    // ==================== OnChainId 步骤 ====================
    
    /** 部署 Identity 实现合约 */
    String ONCHAIN_ID_IDENTITY = "init_onchainid_identity";
    /** 部署 Identity ImplementationAuthority */
    String ONCHAIN_ID_IA = "init_onchainid_ia";
    /** 部署 Identity Factory */
    String ONCHAIN_ID_FACTORY = "init_onchainid_factory";
    /** 部署 ClaimIssuer */
    String ONCHAIN_ID_CLAIM_ISSUER = "init_onchainid_claim_issuer";
    
    // ==================== ERC3643 实现合约步骤 ====================
    
    /** 部署 Token 实现合约 */
    String ERC3643_TOKEN_IMPL = "init_erc3643_token_impl";
    /** 部署 ClaimTopicsRegistry 实现合约 */
    String ERC3643_CTR_IMPL = "init_erc3643_ctr_impl";
    /** 部署 TrustedIssuersRegistry 实现合约 */
    String ERC3643_TIR_IMPL = "init_erc3643_tir_impl";
    /** 部署 IdentityRegistryStorage 实现合约 */
    String ERC3643_IRS_IMPL = "init_erc3643_irs_impl";
    /** 部署 IdentityRegistry 实现合约 */
    String ERC3643_IR_IMPL = "init_erc3643_ir_impl";
    /** 部署 ModularCompliance 实现合约 */
    String ERC3643_MC_IMPL = "init_erc3643_mc_impl";
    
    // ==================== ERC3643 基础设施步骤 ====================
    
    /** 部署 TREXImplementationAuthority */
    String ERC3643_IA = "init_erc3643_ia";
    /** 添加版本到 TREX IA */
    String ERC3643_IA_VERSION = "init_erc3643_ia_version";
    /** 部署 TREXFactory */
    String ERC3643_FACTORY = "init_erc3643_factory";
    /** 部署 IAFactory */
    String ERC3643_IA_FACTORY = "init_erc3643_ia_factory";
    /** 配置 TREX IA */
    String ERC3643_IA_CONFIG = "init_erc3643_ia_config";
    /** 部署首个 IdentityRegistryStorage */
    String FIRST_IRS = "init_first_irs";
}
