package cc.bamboo.module.project.controller.admin.projectorderbalance.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户项目余额表分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectOrderBalancePageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "21626")
    private Long userId;

    @Schema(description = "项目ID", example = "335")
    private Long projectId;

    @Schema(description = "订单ID", example = "20110")
    private Long orderId;

    @Schema(description = "本金金额(元)")
    private BigDecimal principalAmount;

    @Schema(description = "持有金额")
    private BigDecimal holdAmount;

    @Schema(description = "购买份额")
    private Integer buyQuantity;

    @Schema(description = "当前持有份额(份)（赎回后扣减）")
    private Integer holdQuantity;

    @Schema(description = "累计总收益(元)（含未提取）")
    private BigDecimal totalIncome;

    @Schema(description = "已提取分红(元)")
    private BigDecimal withdrawnDividend;

    @Schema(description = "冻结的分红(元)")
    private String freezeDividend;

    @Schema(description = "未提取分红(元)（=总收益-已提取）")
    private BigDecimal unwithdrawnDividend;

    @Schema(description = "累计赎回本金(元)（赎回时累加）")
    private BigDecimal totalRedemptionAmount;

    @Schema(description = "最后一次收益计算时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] lastIncomeCalcTime;

    @Schema(description = "最后一次分红提取时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] lastWithdrawTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}