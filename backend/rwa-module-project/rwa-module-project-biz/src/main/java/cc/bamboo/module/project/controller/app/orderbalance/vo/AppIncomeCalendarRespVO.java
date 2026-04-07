package cc.bamboo.module.project.controller.app.orderbalance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "用户 APP - 收益日历 Response VO")
public class AppIncomeCalendarRespVO {

    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-10-01")
    private LocalDate date;

    @Schema(description = "当日总收益", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    private List<AppAssetByCurrencyRespVO> dailyTotalIncome;
}
