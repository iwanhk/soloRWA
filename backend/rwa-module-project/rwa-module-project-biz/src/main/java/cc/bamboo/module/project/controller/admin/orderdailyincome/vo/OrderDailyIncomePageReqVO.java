package cc.bamboo.module.project.controller.admin.orderdailyincome.vo;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 订单每日收益统计分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OrderDailyIncomePageReqVO extends PageParam {

    @Schema(description = "订单id", example = "25769")
    private Long orderId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID", example = "19807")
    private Long userId;

    @Schema(description = "项目ID", example = "31425")
    private Long projectId;

    @Schema(description = "收益日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDate[] incomeDate;

    @Schema(description = "收益id", example = "25670")
    private Long projectRevenueId;

    @Schema(description = "当日持有份额")
    private Integer holdQuantity;

    @Schema(description = "收益发放时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] issueTime;

    @Schema(description = "当日收益")
    private BigDecimal dailyIncome;

    @Schema(description = "当日收益率")
    private BigDecimal incomeRate;

    @Schema(description = "累计收益")
    private BigDecimal cumulativeIncome;

    @Schema(description = "收益状态", example = "2")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}