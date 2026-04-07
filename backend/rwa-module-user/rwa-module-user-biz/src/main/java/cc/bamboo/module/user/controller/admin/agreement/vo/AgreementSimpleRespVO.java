package cc.bamboo.module.user.controller.admin.agreement.vo;

import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 系统协议表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AgreementSimpleRespVO {

    @Schema(description = "协议ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "15539")
    @ExcelProperty("协议ID")
    private Long id;

    @Schema(description = "协议类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "协议类型", converter = DictConvert.class)
    @DictFormat("agreement_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer agreementType;

    @Schema(description = "协议标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("协议标题")
    private String agreementTitle;

    @Schema(description = "协议内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("协议内容")
    private String agreementContent;


}