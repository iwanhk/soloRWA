package cc.bamboo.module.chain.service.token.dto;

import lombok.Data;

/**
 * 发行 Token 请求 DTO
 * 
 * @author Swolf
 */
@Data
public class MintTokenReqDTO {
    
    /**
     * Token ID
     */
    private Long tokenId;
    
    /**
     * 接收地址
     */
    private String toAddress;
    
    /**
     * 发行数量（字符串，支持大数）
     */
    private String amount;
    
    /**
     * 代理地址（可选，默认使用 Token 配置的第一个代理）
     */
    private String agentAddress;
}
