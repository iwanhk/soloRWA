package cc.bamboo.module.chain.service.identity.dto;

import lombok.Data;

import java.util.List;

/**
 * 关联钱包响应 DTO
 * 
 * @author Swolf
 */
@Data
public class LinkWalletRespDTO {
    
    /**
     * 旧钱包地址
     */
    private String oldWalletAddress;
    
    /**
     * 新钱包地址
     */
    private String newWalletAddress;
    
    /**
     * Identity 合约地址
     */
    private String identityAddress;
    
    /**
     * 注册结果列表
     */
    private List<TokenRegistrationResult> registrationResults;
    
    /**
     * Token 注册结果
     */
    @Data
    public static class TokenRegistrationResult {
        /**
         * Token ID
         */
        private Long tokenId;
        
        /**
         * Token 合约地址
         */
        private String tokenAddress;
        
        /**
         * 交易哈希
         */
        private String transactionHash;
        
        /**
         * 区块号
         */
        private Long blockNumber;
        
        /**
         * 错误信息（如果失败）
         */
        private String error;
    }
}
