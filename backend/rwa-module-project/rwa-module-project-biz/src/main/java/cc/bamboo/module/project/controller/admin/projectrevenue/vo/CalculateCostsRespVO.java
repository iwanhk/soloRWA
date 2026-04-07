package cc.bamboo.module.project.controller.admin.projectrevenue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 自动计算成本响应 VO
 */
@Schema(description = "管理后台 - 自动计算成本 Response VO")
@Data
public class CalculateCostsRespVO {

    @Schema(description = "电力成本", example = "1200.00")
    private BigDecimal electricityCost;

    @Schema(description = "人力成本", example = "500.00")
    private BigDecimal peopleCost;

    @Schema(description = "项目方收益", example = "8300.00")
    private BigDecimal projectRevenue;

    @Schema(description = "可发放收益", example = "8300.00")
    private BigDecimal availableRevenue;
}
