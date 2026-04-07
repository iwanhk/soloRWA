package cc.bamboo.module.chain.service.identity.dto;

import lombok.Data;

import java.util.List;

/**
 * 关联钱包响应 DTO
 * 
 * @author Swolf
 */
@Data
public class LinkNewWalletRespDTO {


    /**
     * 交易哈希
     */
    private String transactionHash;


    /**
     * 错误信息（如果失败）
     */
    private String error;

    /**
     * 0 成功 1失败
     */
    private int result;
}
