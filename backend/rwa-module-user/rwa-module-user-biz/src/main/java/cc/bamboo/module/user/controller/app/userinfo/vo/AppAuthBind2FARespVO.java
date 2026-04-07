package cc.bamboo.module.user.controller.app.userinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户 APP - 绑定2FA Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppAuthBind2FARespVO {

    @Schema(description = "2FA密钥(Base32格式)", requiredMode = Schema.RequiredMode.REQUIRED, example = "JBSWY3DPEHPK3PXP")
    private String secret;

    @Schema(description = "2FA二维码URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "otpauth://totp/...")
    private String qrCodeUrl;

}
