package cc.bamboo.module.chain.controller.admin.identity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 添加 ClaimIssuer 请求 VO
 * 
 * @author Swolf
 */
@Schema(description = "管理后台 - 添加 ClaimIssuer 请求")
@Data
public class AddClaimIssuerReqVO {
    
    @Schema(description = "ClaimIssuer 钱包地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "钱包地址不能为空")
    private String address;
}
