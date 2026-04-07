package cc.bamboo.module.chain.service.trex.dto;

import lombok.Data;

import java.util.List;

/**
 * 部署 Token 请求 DTO
 * 
 * @author Swolf
 */
@Data
public class DeployTokenReqDTO {
    
    /**
     * 盐值，用于确定性部署
     */
    private String salt;
    
    /**
     * Token 所有者地址
     */
    private String ownerAddress;
    
    /**
     * Token 名称
     */
    private String name;
    
    /**
     * Token 符号
     */
    private String symbol;
    
    /**
     * 小数位数，默认 18
     */
    private Integer decimals = 18;
    
    /**
     * Token 代理人列表
     */
    private List<String> tokenAgents;
    
    /**
     * 声明主题列表（bytes32 哈希）
     */
    private List<String> claimTopics;
    
    /**
     * 可信签发者列表（Identity 合约地址）
     */
    private List<String> issuers;
    
    /**
     * 签发者可签发的声明主题
     * 二维数组，每个签发者对应一组声明主题
     */
    private List<List<String>> issuerClaims;

    private Long projectId;
}
