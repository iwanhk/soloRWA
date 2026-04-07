package cc.bamboo.module.project.controller.admin.projectorderbalancelog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 用户项目余额记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectOrderBalanceLogRespVO {

    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22623")
    @ExcelProperty("记录ID")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "16668")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "项目ID", example = "31372")
    @ExcelProperty("项目ID")
    private Long projectId;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20226")
    @ExcelProperty("订单ID")
    private Long orderId;

    @Schema(description = "操作金额")
    @ExcelProperty("操作金额")
    private BigDecimal amount;

    @Schema(description = "操作后金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("操作后金额")
    private BigDecimal afterAmount;

    @Schema(description = "类型", example = "2")
    @ExcelProperty(value = "类型", converter = DictConvert.class)
    @DictFormat("biz_balance_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer type;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}