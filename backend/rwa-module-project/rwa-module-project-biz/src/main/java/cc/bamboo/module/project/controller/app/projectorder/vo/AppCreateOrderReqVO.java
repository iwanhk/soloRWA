package cc.bamboo.module.project.controller.app.projectorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 用户 APP - 创建订单 Request VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 创建订单 Request VO")
@Data
public class AppCreateOrderReqVO {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @Schema(description = "钱包地址ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long addressId;

    @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量必须大于0")
    private Integer quantity;


}
