package cc.bamboo.module.system.api.mail.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "RPC 服务 - 邮件验证码创建 Request DTO")
@Data
public class MailCodeCreateReqDTO {
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mail;

    @Schema(description = "场景", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer scene;
}
