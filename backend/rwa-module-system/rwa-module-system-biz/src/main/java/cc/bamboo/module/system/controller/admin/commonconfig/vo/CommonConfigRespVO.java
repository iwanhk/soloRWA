package cc.bamboo.module.system.controller.admin.commonconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 参数配置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CommonConfigRespVO {

    @Schema(description = "参数主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21437")
    @ExcelProperty("参数主键")
    private Long id;

    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("参数名称")
    private String name;

    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("参数键名")
    private String configKey;

    @Schema(description = "参数键值", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("参数键值")
    private String value;

    @Schema(description = "前端可用", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("前端可用")
    private Integer isApp;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}