package cc.bamboo.module.system.api.mail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MailCodeUseReqDTO {
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300@qq.com")
    private String mail;

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "验证码不能为空")
    private String code;

    @Schema(description = "场景", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer scene;
}
