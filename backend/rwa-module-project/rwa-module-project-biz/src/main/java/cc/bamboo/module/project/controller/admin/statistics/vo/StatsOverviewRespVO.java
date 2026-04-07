package cc.bamboo.module.project.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 概览统计数据 VO
 */
@Schema(description = "管理后台 - 概览统计数据 Response VO")
@Data
public class StatsOverviewRespVO {

    @Schema(description = "项目总数", example = "156")
    private Long projectCount;

    @Schema(description = "订单总量", example = "3842")
    private Long orderCount;

    @Schema(description = "交易总额(元)", example = "2856432.50")
    private BigDecimal totalAmount;
}
