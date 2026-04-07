package cc.bamboo.module.user.controller.app.userchain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/1/9 11:19
 * @description
 */
@Schema(description = "用户 APP - 绑定链地址")
@Data
public class AppBindAddressReqVO {

    @Schema(description = "链地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String chainAddress;

    @Schema(description = "签名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String sign;
}
