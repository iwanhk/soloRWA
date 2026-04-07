package cc.bamboo.module.project.controller.app.exchange.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * USDT汇率响应 VO
 *
 * @author Swolf
 */
@Schema(description = "APP - USDT汇率 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateRespVO {

    @Schema(description = "USDT对美元汇率", example = "0.999322")
    private BigDecimal usd;

    @Schema(description = "USDT对人民币汇率", example = "7.02")
    private BigDecimal cny;

    @Schema(description = "USDT对港币汇率", example = "7.77")
    private BigDecimal hkd;

    @Schema(description = "USDT对比特币汇率", example = "0.000082")
    private BigDecimal btc;

}
