package cc.bamboo.module.user.controller.app.userinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 APP - 协议列表 Response VO")
@Data
public class AppAgreementRespVO {

    @Schema(description = "协议ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "协议名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户注册协议")
    private String agreementTitle;

    @Schema(description = "协议内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "协议内容...")
    private String agreementContent;

    private String agreementKey;

}
