package cc.bamboo.module.project.controller.app.orderbalance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Data
@Schema(description = "用户 APP - 收益日历 Request VO")
public class AppIncomeCalendarReqVO {

    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-10-01")
    @NotNull(message = "开始日期不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startDate;

    @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-10-31")
    @NotNull(message = "结束日期不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endDate;

    @Schema(description = "项目编号", example = "1024")
    private Long projectId;

    @Schema(description = "订单编号", example = "1024")
    private Long orderId;
}
