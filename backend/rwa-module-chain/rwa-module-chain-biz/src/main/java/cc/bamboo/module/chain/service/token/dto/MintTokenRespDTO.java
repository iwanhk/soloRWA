package cc.bamboo.module.chain.service.token.dto;

import lombok.Data;

/**
 * 发行 Token 响应 DTO
 * 
 * @author Swolf
 */
@Data
public class MintTokenRespDTO {
    
    /**
     * Token ID
     */
    private Long tokenId;
    
    /**
     * 接收地址
     */
    private String toAddress;
    
    /**
     * 发行数量
     */
    private String amount;
    
    /**
     * 代理地址
     */
    private String agentAddress;
    
    /**
     * 交易哈希
     */
    private String transactionHash;
    
    /**
     * 区块号
     */
    private Long blockNumber;
}
