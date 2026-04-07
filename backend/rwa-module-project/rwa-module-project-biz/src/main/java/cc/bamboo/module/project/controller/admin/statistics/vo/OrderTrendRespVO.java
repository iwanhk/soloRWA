package cc.bamboo.module.project.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单趋势数据 VO
 */
@Schema(description = "管理后台 - 订单趋势数据 Response VO")
@Data
public class OrderTrendRespVO {

    @Schema(description = "日期", example = "01/20")
    private String date;

    @Schema(description = "订单数量", example = "120")
    private Long orderCount;

    @Schema(description = "交易金额", example = "50000.00")
    private BigDecimal amount;
}
