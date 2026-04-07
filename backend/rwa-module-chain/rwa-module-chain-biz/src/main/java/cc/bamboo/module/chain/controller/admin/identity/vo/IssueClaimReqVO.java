package cc.bamboo.module.chain.controller.admin.identity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 签发 Claim 请求 VO
 * 
 * @author Swolf
 */
@Schema(description = "管理后台 - 签发 Claim 请求")
@Data
public class IssueClaimReqVO {
    
    @Schema(description = "用户 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户 ID 不能为空")
    private Long userId;
    
    @Schema(description = "声明主题（bytes32 哈希）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "声明主题不能为空")
    private String topic;
    
    @Schema(description = "声明数据（十六进制字符串）")
    private String data = "0x";
    
    @Schema(description = "声明 URI")
    private String uri = "";
    
    @Schema(description = "签名方案，默认 ECDSA_SIGNATURE = 1")
    private Integer scheme = 1;
}
