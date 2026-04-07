package cc.bamboo.module.project.controller.app.exchange.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 法币汇率响应 VO
 *
 * @author Swolf
 */
@Schema(description = "APP - 法币汇率 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiatExchangeRateRespVO {

    @Schema(description = "人民币汇率(1 USD = ? CNY)", example = "7.25")
    private BigDecimal cny;

    @Schema(description = "港币汇率(1 USD = ? HKD)", example = "7.82")
    private BigDecimal hkd;

}
