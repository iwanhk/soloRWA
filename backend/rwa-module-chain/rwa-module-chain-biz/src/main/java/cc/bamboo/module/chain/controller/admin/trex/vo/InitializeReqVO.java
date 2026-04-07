package cc.bamboo.module.chain.controller.admin.trex.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 初始化系统请求 VO
 * 
 * @author Swolf
 */
@Schema(description = "管理后台 - 初始化系统请求")
@Data
public class InitializeReqVO {
    
    @Schema(description = "ClaimIssuer 管理密钥（可选，默认使用 deployer 地址）")
    private String claimIssuerManagementKey;
}
