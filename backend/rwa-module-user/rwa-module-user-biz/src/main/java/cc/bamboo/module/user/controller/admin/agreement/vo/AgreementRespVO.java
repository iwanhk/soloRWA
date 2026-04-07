package cc.bamboo.module.user.controller.admin.agreement.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 系统协议表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AgreementRespVO {

    @Schema(description = "协议ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "15539")
    @ExcelProperty("协议ID")
    private Long id;

    @Schema(description = "协议类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "协议类型", converter = DictConvert.class)
    @DictFormat("agreement_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer agreementType;

    private String agreementKey;

    @Schema(description = "协议标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("协议标题")
    private String agreementTitle;

    @Schema(description = "协议内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("协议内容")
    private String agreementContent;

    @Schema(description = "协议版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("协议版本号")
    private String version;

    @Schema(description = "是否当前生效版本：1-是 0-否（同一类型仅1个生效版本）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否当前生效版本：1-是 0-否（同一类型仅1个生效版本）")
    private Boolean isCurrent;

    @Schema(description = "生效时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("生效时间")
    private LocalDateTime effectiveTime;

    @Schema(description = "过期时间")
    @ExcelProperty("过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}