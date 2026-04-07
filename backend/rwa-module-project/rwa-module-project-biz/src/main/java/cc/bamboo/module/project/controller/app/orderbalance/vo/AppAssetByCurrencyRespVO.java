package cc.bamboo.module.project.controller.app.orderbalance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 按币种分组的资产 VO
 *
 * @author Swolf
 */
@Data
@Schema(description = "用户 APP - 按币种分组的资产 Response VO")
@NoArgsConstructor
@AllArgsConstructor
public class AppAssetByCurrencyRespVO {

    @Schema(description = "投资币种", requiredMode = Schema.RequiredMode.REQUIRED, example = "USDT")
    private String coinCode;

    @Schema(description = "该币种的持有金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000.00")
    private BigDecimal total;

}
