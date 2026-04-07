package cc.bamboo.module.project.controller.admin.projectrevenue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 矿池收益计算响应 VO
 *
 * @author Swolf
 */
@Schema(description = "管理后台 - 矿池收益计算 Response VO")
@Data
public class PoolRevenueCalcRespVO {

    @Schema(description = "币种收益", example = "0.01234567")
    private BigDecimal coinRevenue;

    @Schema(description = "币种", example = "BTC")
    private String coin;

}
