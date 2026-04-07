package cc.bamboo.module.chain.constant;

/**
 * Identity 相关常量 (ERC-734/735)
 * 
 * @author Swolf
 */
public interface IdentityConstants {
    
    // ==================== Key Purpose (ERC-734) ====================
    
    /** 管理密钥 */
    int KEY_PURPOSE_MANAGEMENT = 1;
    /** 操作密钥 */
    int KEY_PURPOSE_ACTION = 2;
    /** 声明密钥 */
    int KEY_PURPOSE_CLAIM = 3;
    /** 加密密钥 */
    int KEY_PURPOSE_ENCRYPTION = 4;
    
    // ==================== Key Type (ERC-734) ====================
    
    /** ECDSA 密钥 */
    int KEY_TYPE_ECDSA = 1;
    /** RSA 密钥 */
    int KEY_TYPE_RSA = 2;
    
    // ==================== Claim Scheme (ERC-735) ====================
    
    /** ECDSA 签名方案 */
    int CLAIM_SCHEME_ECDSA_SIGNATURE = 1;
    
    // ==================== Identity Type ====================
    
    /** ClaimIssuer 类型 */
    String IDENTITY_TYPE_CLAIM_ISSUER = "ClaimIssuer";
    /** User 类型 */
    String IDENTITY_TYPE_USER = "User";
    
    // ==================== Status ====================
    
    /** 待处理状态 */
    String STATUS_PENDING = "pending";
    /** 进行中状态 */
    String STATUS_IN_PROGRESS = "in_progress";
    /** 已完成状态 */
    String STATUS_COMPLETED = "completed";
    /** 失败状态 */
    String STATUS_FAILED = "failed";
    /** 已部署状态 */
    String STATUS_DEPLOYED = "deployed";
    /** 活跃状态 */
    String STATUS_ACTIVE = "active";
    /** 非活跃状态 */
    String STATUS_INACTIVE = "inactive";
    
    // ==================== IRS 限制 ====================
    
    /** 每个 IdentityRegistryStorage 最多绑定的 Token 数量 */
    int MAX_TOKENS_PER_IRS = 300;
}
