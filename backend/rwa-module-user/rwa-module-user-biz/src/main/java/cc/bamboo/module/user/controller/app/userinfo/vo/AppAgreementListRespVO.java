package cc.bamboo.module.user.controller.app.userinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 APP - 协议列表 Response VO")
@Data
public class AppAgreementListRespVO {

    @Schema(description = "协议ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "协议名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户注册协议")
    private String agreementTitle;

    private String agreementKey;


}
