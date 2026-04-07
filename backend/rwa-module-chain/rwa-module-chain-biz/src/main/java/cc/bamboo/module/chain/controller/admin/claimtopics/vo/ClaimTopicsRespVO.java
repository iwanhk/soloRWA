package cc.bamboo.module.chain.controller.admin.claimtopics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 声明主题 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ClaimTopicsRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6890")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "主题名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("主题名称")
    private String name;

    @Schema(description = "主题值", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主题值")
    private String value;

    @Schema(description = "主题哈希", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主题哈希")
    private String topic;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}