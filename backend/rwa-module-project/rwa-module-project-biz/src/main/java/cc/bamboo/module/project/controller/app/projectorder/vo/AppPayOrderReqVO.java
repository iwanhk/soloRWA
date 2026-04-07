package cc.bamboo.module.project.controller.app.projectorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户 APP - 支付确认 Request VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 支付确认 Request VO")
@Data
public class AppPayOrderReqVO {

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "支付凭证文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "支付凭证不能为空")
    private MultipartFile payVoucher;

    @Schema(description = "合同号", requiredMode = Schema.RequiredMode.REQUIRED, example = "CONTRACT20231201123456")
    @NotBlank(message = "合同号不能为空")
    @Size(max = 100, message = "合同号长度不能超过100个字符")
    private String contractNo;

    @Schema(description = "手机验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String smsCode;

    private String emailCode;

    private String ftaCode;

}
