package cc.bamboo.module.project.controller.app.projectorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户 APP - 生成 Web3 签名 Response VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 生成 Web3 签名 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppGenerateSignatureRespVO {

    @Schema(description = "签名字符串", requiredMode = Schema.RequiredMode.REQUIRED, example = "0xabcdef...")
    private String signature;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ORD20231201123456")
    private String orderNo;

}
