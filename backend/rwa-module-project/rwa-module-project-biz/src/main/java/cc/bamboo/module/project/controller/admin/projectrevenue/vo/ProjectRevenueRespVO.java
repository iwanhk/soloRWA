package cc.bamboo.module.project.controller.admin.projectrevenue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 项目收益 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectRevenueRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "32082")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "收益日期")
    @ExcelProperty("收益日期")
    private LocalDate revenueDate;

    @Schema(description = "总收益")
    @ExcelProperty("总收益")
    private BigDecimal revenue;

    @Schema(description = "产品id", example = "19616")
    @ExcelProperty("产品id")
    private Long projectId;

    @Schema(description = "产品名称", example = "赵六")
    @ExcelProperty("产品名称")
    private String projectName;

    @Schema(description = "电力成本")
    @ExcelProperty("电力成本")
    private BigDecimal electricityCost;

    @Schema(description = "人力成本")
    @ExcelProperty("人力成本")
    private BigDecimal peopleCost;

    @Schema(description = "可发放收益")
    @ExcelProperty("可发放收益")
    private BigDecimal availableRevenue;

    @Schema(description = "币种收益", example = "100.00")
    @ExcelProperty("币种收益")
    private BigDecimal coinRevenue;

    @Schema(description = "项目收益", example = "50.00")
    @ExcelProperty("项目收益")
    private BigDecimal projectRevenue;

    @Schema(description = "是否发放收益")
    @ExcelProperty("是否发放收益")
    private Integer isSend;

    @Schema(description = "是否同步收益")
    @ExcelProperty("是否同步收益")
    private Integer isSync;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "币种", example = "USDT")
    @ExcelProperty("币种")
    private String coinCode;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}