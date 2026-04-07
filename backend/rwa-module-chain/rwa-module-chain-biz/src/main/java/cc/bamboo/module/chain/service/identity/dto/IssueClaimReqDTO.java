package cc.bamboo.module.chain.service.identity.dto;

import lombok.Data;

/**
 * 签发 Claim 请求 DTO
 * 
 * @author Swolf
 */
@Data
public class IssueClaimReqDTO {
    
    /**
     * ClaimIssuer ID
     */
    private Long claimIssuerId;
    
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 声明主题（bytes32 哈希）
     */
    private String topic;
    
    /**
     * 声明数据（十六进制字符串）
     */
    private String data = "0x";
    
    /**
     * 声明 URI
     */
    private String uri = "";
    
    /**
     * 签名方案，默认 ECDSA_SIGNATURE = 1
     */
    private Integer scheme = 1;
}
