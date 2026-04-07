package cc.bamboo.module.project.controller.admin.orderdailyincome.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 订单每日收益统计新增/修改 Request VO")
@Data
public class OrderDailyIncomeSaveReqVO {

    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3892")
    private Long id;

    @Schema(description = "订单id", requiredMode = Schema.RequiredMode.REQUIRED, example = "25769")
    @NotNull(message = "订单id不能为空")
    private Long orderId;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "订单号不能为空")
    private String orderNo;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19807")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "31425")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @Schema(description = "收益日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "收益日期不能为空")
    private LocalDate incomeDate;

    @Schema(description = "收益id", example = "25670")
    private Long projectRevenueId;

    @Schema(description = "当日持有份额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "当日持有份额不能为空")
    private Integer holdQuantity;

    @Schema(description = "收益发放时间")
    private LocalDateTime issueTime;

    @Schema(description = "当日收益", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "当日收益不能为空")
    private BigDecimal dailyIncome;

    @Schema(description = "当日收益率", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "当日收益率不能为空")
    private BigDecimal incomeRate;

    @Schema(description = "累计收益", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "累计收益不能为空")
    private BigDecimal cumulativeIncome;

    @Schema(description = "收益状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "收益状态不能为空")
    private Integer status;

}