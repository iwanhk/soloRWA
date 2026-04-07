package cc.bamboo.module.chain.controller.admin.identity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 关联钱包请求 VO
 * 
 * @author Swolf
 */
@Schema(description = "管理后台 - 关联钱包请求")
@Data
public class LinkWalletReqVO {
    
    @Schema(description = "新钱包地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newWalletAddress;
    
    @Schema(description = "旧钱包地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldWalletAddress;

    private Long id;
}
