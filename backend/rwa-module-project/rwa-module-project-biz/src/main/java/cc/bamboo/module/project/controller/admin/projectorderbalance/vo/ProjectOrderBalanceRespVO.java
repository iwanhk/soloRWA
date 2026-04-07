package cc.bamboo.module.project.controller.admin.projectorderbalance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 用户项目余额表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectOrderBalanceRespVO {

    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24674")
    @ExcelProperty("记录ID")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21626")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "项目ID", example = "335")
    @ExcelProperty("项目ID")
    private Long projectId;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20110")
    @ExcelProperty("订单ID")
    private Long orderId;

    @Schema(description = "本金金额(元)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("本金金额(元)")
    private BigDecimal principalAmount;

    @Schema(description = "持有金额")
    @ExcelProperty("持有金额")
    private BigDecimal holdAmount;

    @Schema(description = "购买份额")
    @ExcelProperty("购买份额")
    private Integer buyQuantity;

    @Schema(description = "当前持有份额(份)（赎回后扣减）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("当前持有份额(份)（赎回后扣减）")
    private Integer holdQuantity;

    @Schema(description = "累计总收益(元)（含未提取）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("累计总收益(元)（含未提取）")
    private BigDecimal totalIncome;

    @Schema(description = "已提取分红(元)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("已提取分红(元)")
    private BigDecimal withdrawnDividend;

    @Schema(description = "冻结的分红(元)")
    @ExcelProperty("冻结的分红(元)")
    private String freezeDividend;

    @Schema(description = "未提取分红(元)（=总收益-已提取）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("未提取分红(元)（=总收益-已提取）")
    private BigDecimal unwithdrawnDividend;

    @Schema(description = "累计赎回本金(元)（赎回时累加）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("累计赎回本金(元)（赎回时累加）")
    private BigDecimal totalRedemptionAmount;

    @Schema(description = "最后一次收益计算时间")
    @ExcelProperty("最后一次收益计算时间")
    private LocalDateTime lastIncomeCalcTime;

    @Schema(description = "最后一次分红提取时间")
    @ExcelProperty("最后一次分红提取时间")
    private LocalDateTime lastWithdrawTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}