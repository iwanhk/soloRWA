package cc.bamboo.module.chain.service.identity.dto;

import lombok.Data;

import java.util.List;

/**
 * 身份操作响应 DTO
 * 
 * @author Swolf
 */
@Data
public class IdentityOperationRespDTO {
    
    /**
     * 身份 ID
     */
    private Long id;
    
    /**
     * Identity 合约地址
     */
    private String contractAddress;
    
    /**
     * 管理密钥
     */
    private String managementKey;
    
    /**
     * 合约是否已部署
     */
    private Boolean contractDeployed;
    
    /**
     * ClaimKey 是否已设置
     */
    private Boolean claimKeySetup;
    
    /**
     * 执行的操作列表
     */
    private List<String> operations;

    private List<Long> topicIds;

    private String hash;
}
