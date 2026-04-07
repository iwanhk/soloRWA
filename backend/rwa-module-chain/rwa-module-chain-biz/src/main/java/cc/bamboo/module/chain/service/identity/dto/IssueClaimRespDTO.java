package cc.bamboo.module.chain.service.identity.dto;

import lombok.Data;

/**
 * 签发 Claim 响应 DTO
 * 
 * @author Swolf
 */
@Data
public class IssueClaimRespDTO {
    
    /**
     * ClaimIssuer ID
     */
    private Long claimIssuerId;
    
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 用户 Identity 合约地址
     */
    private String userIdentityAddress;
    
    /**
     * 声明主题
     */
    private String topic;
    
    /**
     * 声明数据
     */
    private String data;
    
    /**
     * 声明 URI
     */
    private String uri;
    
    /**
     * 签名方案
     */
    private Integer scheme;
    
    /**
     * 签发者 Identity 合约地址
     */
    private String issuerAddress;
    
    /**
     * 签发者钱包地址
     */
    private String issuerWalletAddress;
    
    /**
     * 交易哈希
     */
    private String transactionHash;
    
    /**
     * 区块号
     */
    private Long blockNumber;
}
