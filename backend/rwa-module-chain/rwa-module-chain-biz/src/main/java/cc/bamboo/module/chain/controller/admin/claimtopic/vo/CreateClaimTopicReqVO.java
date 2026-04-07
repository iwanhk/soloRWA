package cc.bamboo.module.chain.controller.admin.claimtopic.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 创建 ClaimTopic 请求 VO
 * 
 * @author Swolf
 */
@Schema(description = "管理后台 - 创建 ClaimTopic 请求")
@Data
public class CreateClaimTopicReqVO {
    
    @Schema(description = "主题名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "平台验证")
    @NotBlank(message = "主题名称不能为空")
    private String name;
    
    @Schema(description = "主题值", requiredMode = Schema.RequiredMode.REQUIRED, example = "PLATFORM_VERIFIED")
    @NotBlank(message = "主题值不能为空")
    private String value;
}
