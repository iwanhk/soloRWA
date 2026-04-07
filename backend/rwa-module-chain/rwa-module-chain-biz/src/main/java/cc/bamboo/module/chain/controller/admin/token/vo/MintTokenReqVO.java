package cc.bamboo.module.chain.controller.admin.token.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 发行 Token 请求 VO
 * 
 * @author Swolf
 */
@Schema(description = "管理后台 - 发行 Token 请求")
@Data
public class MintTokenReqVO {
    
    @Schema(description = "接收地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "接收地址不能为空")
    private String toAddress;
    
    @Schema(description = "发行数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "发行数量不能为空")
    private String amount;
    
    @Schema(description = "代理地址（可选）")
    private String agentAddress;
}
