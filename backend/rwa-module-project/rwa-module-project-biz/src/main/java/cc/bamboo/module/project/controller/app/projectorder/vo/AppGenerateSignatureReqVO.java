package cc.bamboo.module.project.controller.app.projectorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 用户 APP - 生成 Web3 签名 Request VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 生成 Web3 签名 Request VO")
@Data
public class AppGenerateSignatureReqVO {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @Schema(description = "钱包地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "0x1234567890abcdef1234567890abcdef12345678")
    @NotBlank(message = "钱包地址不能为空")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "钱包地址格式无效")
    private String chainAddress;

    @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量必须大于0")
    private Integer quantity;

}
