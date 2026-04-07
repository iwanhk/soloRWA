package cc.bamboo.module.project.controller.app.orderbalance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "用户 APP - 每日收益明细 Response VO")
public class AppDailyIncomeRespVO {

    @Schema(description = "项目编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long projectId;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Bamboo Project")
    private String projectName;

    @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long orderId;

    @Schema(description = "收益日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-10-01")
    private LocalDate incomeDate;

    @Schema(description = "订单当日收益", requiredMode = Schema.RequiredMode.REQUIRED, example = "10.00")
    private BigDecimal orderDailyIncome;


    @Schema(description = "收益币种")
    private String earningCurrency;

    private String investmentCurrency;

    @Schema(description = "持有金额")
    private BigDecimal holdAmount;
}
