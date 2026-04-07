package cc.bamboo.module.user.controller.app.userinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Schema(description = "用户 APP - 验证2FA Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppAuthVerify2FAReqVO {

    @Schema(description = "2FA验证码(6位数字)", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "2FA验证码不能为空")
    @Pattern(regexp = "^[0-9]{6}$", message = "2FA验证码必须是6位数字")
    private String code;

}
