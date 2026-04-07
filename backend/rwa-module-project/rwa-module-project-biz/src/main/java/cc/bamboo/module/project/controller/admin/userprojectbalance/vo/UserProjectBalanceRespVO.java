package cc.bamboo.module.project.controller.admin.userprojectbalance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 用户项目余额表（本金/收益汇总） Response VO")
@Data
@ExcelIgnoreUnannotated
public class UserProjectBalanceRespVO {

    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4851")
    @ExcelProperty("记录ID")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "14436")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "项目ID（关联project_core.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "340")
    @ExcelProperty("项目ID（关联project_core.id）")
    private Long projectId;

    @Schema(description = "本金金额(元)（=申购份额×项目单价）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("本金金额(元)（=申购份额×项目单价）")
    private BigDecimal principalAmount;

    @Schema(description = "当前持有份额(份)（赎回后扣减）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("当前持有份额(份)（赎回后扣减）")
    private Integer holdQuantity;

    @Schema(description = "累计总收益(元)（含未提取）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("累计总收益(元)（含未提取）")
    private BigDecimal totalIncome;

    @Schema(description = "已提取分红(元)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("已提取分红(元)")
    private BigDecimal withdrawnDividend;

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

    @Schema(description = "余额状态：1-正常 2-已赎回 3-冻结", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("余额状态：1-正常 2-已赎回 3-冻结")
    private Integer balanceStatus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}