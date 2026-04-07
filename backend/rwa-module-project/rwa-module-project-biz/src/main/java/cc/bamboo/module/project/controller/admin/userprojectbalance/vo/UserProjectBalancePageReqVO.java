package cc.bamboo.module.project.controller.admin.userprojectbalance.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户项目余额表（本金/收益汇总）分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserProjectBalancePageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "14436")
    private Long userId;

    @Schema(description = "项目ID（关联project_core.id）", example = "340")
    private Long projectId;

    @Schema(description = "本金金额(元)（=申购份额×项目单价）")
    private BigDecimal principalAmount;

    @Schema(description = "当前持有份额(份)（赎回后扣减）")
    private Integer holdQuantity;

    @Schema(description = "累计总收益(元)（含未提取）")
    private BigDecimal totalIncome;

    @Schema(description = "已提取分红(元)")
    private BigDecimal withdrawnDividend;

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

    @Schema(description = "余额状态：1-正常 2-已赎回 3-冻结", example = "2")
    private Integer balanceStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}