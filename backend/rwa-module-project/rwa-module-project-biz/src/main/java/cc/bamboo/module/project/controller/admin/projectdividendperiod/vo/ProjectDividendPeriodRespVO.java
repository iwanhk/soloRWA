package cc.bamboo.module.project.controller.admin.projectdividendperiod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 分红周期 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectDividendPeriodRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4912")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "项目id", example = "15942")
    @ExcelProperty("项目id")
    private Long projectId;

    @Schema(description = "分红期数")
    @ExcelProperty("分红期数")
    private Integer periodSeq;

    @Schema(description = "解锁日期")
    @ExcelProperty("解锁日期")
    private LocalDate unlockDate;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}