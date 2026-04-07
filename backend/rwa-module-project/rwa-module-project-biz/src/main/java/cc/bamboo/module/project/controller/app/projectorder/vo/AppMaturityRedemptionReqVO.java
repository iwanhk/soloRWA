package cc.bamboo.module.project.controller.app.projectorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 用户 APP - 到期赎回 Request VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 到期赎回 Request VO")
@Data
public class AppMaturityRedemptionReqVO {

    @Schema(description = "订单余额ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单余额ID不能为空")
    private Long orderId;


    @Schema(description = "手机验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String smsCode;

    private String emailCode;

    private String ftaCode;
}
