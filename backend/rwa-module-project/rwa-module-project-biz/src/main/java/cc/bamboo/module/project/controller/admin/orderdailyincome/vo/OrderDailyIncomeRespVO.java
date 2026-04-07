package cc.bamboo.module.project.controller.admin.orderdailyincome.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 订单每日收益统计 Response VO")
@Data
@ExcelIgnoreUnannotated
public class OrderDailyIncomeRespVO {

    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3892")
    @ExcelProperty("记录ID")
    private Long id;

    @Schema(description = "订单id", requiredMode = Schema.RequiredMode.REQUIRED, example = "25769")
    @ExcelProperty("订单id")
    private Long orderId;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("订单号")
    private String orderNo;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19807")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "31425")
    @ExcelProperty("项目ID")
    private Long projectId;

    @Schema(description = "收益日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("收益日期")
    private LocalDate incomeDate;

    @Schema(description = "收益id", example = "25670")
    @ExcelProperty("收益id")
    private Long projectRevenueId;

    @Schema(description = "当日持有份额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("当日持有份额")
    private Integer holdQuantity;

    @Schema(description = "收益发放时间")
    @ExcelProperty("收益发放时间")
    private LocalDateTime issueTime;

    @Schema(description = "当日收益", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("当日收益")
    private BigDecimal dailyIncome;

    @Schema(description = "当日收益率", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("当日收益率")
    private BigDecimal incomeRate;

    @Schema(description = "累计收益", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("累计收益")
    private BigDecimal cumulativeIncome;

    @Schema(description = "收益状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "收益状态", converter = DictConvert.class)
    @DictFormat("biz_income_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}