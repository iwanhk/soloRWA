package cc.bamboo.module.user.controller.app.userinfo.vo;

import cc.bamboo.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Schema(description = "用户 APP - 2FA解绑请求 Request VO")
@Data
public class AppAuthUnbind2FAReqVO {


    @Schema(description = "手机验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
/*    @NotEmpty(message = "手机验证码不能为空")
    @Length(min = 4, max = 6, message = "手机验证码长度为 4-6 位")
    @Pattern(regexp = "^[0-9]+$", message = "手机验证码必须都是数字")*/
    private String code;


    @Schema(description = "邮箱验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private String emailCode;

}
