package cc.bamboo.module.project.controller.admin.projectrevenue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 修改可发放收益请求 VO
 */
@Schema(description = "管理后台 - 修改可发放收益 Request VO")
@Data
public class UpdateAvailableRevenueReqVO {

    @Schema(description = "收益ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "收益ID不能为空")
    private Long id;

    @Schema(description = "可发放收益", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000.00")
    @NotNull(message = "可发放收益不能为空")
    private BigDecimal availableRevenue;

    @Schema(description = "备注", example = "手动调整收益")
    private String remark;

    private BigDecimal coinRevenue;

    /**
     * 电力成本
     */
    private BigDecimal electricityCost;
    /**
     * 人力成本
     */
    private BigDecimal peopleCost;
    /**
     * 项目收益
     */
    private BigDecimal projectRevenue;
}
