package cc.bamboo.module.project.controller.admin.projectorderbalance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户项目余额表新增/修改 Request VO")
@Data
public class ProjectOrderBalanceSaveReqVO {

    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24674")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21626")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "项目ID", example = "335")
    private Long projectId;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20110")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "本金金额(元)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "本金金额(元)不能为空")
    private BigDecimal principalAmount;

    @Schema(description = "持有金额")
    private BigDecimal holdAmount;

    @Schema(description = "购买份额")
    private Integer buyQuantity;

    @Schema(description = "当前持有份额(份)（赎回后扣减）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "当前持有份额(份)（赎回后扣减）不能为空")
    private Integer holdQuantity;

    @Schema(description = "累计总收益(元)（含未提取）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "累计总收益(元)（含未提取）不能为空")
    private BigDecimal totalIncome;

    @Schema(description = "已提取分红(元)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "已提取分红(元)不能为空")
    private BigDecimal withdrawnDividend;

    @Schema(description = "冻结的分红(元)")
    private String freezeDividend;

    @Schema(description = "未提取分红(元)（=总收益-已提取）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "未提取分红(元)（=总收益-已提取）不能为空")
    private BigDecimal unwithdrawnDividend;

    @Schema(description = "累计赎回本金(元)（赎回时累加）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "累计赎回本金(元)（赎回时累加）不能为空")
    private BigDecimal totalRedemptionAmount;

    @Schema(description = "最后一次收益计算时间")
    private LocalDateTime lastIncomeCalcTime;

    @Schema(description = "最后一次分红提取时间")
    private LocalDateTime lastWithdrawTime;

}