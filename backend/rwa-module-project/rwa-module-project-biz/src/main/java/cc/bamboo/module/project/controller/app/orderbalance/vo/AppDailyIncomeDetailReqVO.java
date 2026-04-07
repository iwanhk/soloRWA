package cc.bamboo.module.project.controller.app.orderbalance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Data
@Schema(description = "用户 APP - 每日收益明细 Request VO")
public class AppDailyIncomeDetailReqVO {

    @Schema(description = "收益日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-10-01")
    @NotNull(message = "收益日期不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate date;

    @Schema(description = "项目编号", example = "1024")
    private Long projectId;

    @Schema(description = "订单编号", example = "1024")
    private Long orderId;
}
