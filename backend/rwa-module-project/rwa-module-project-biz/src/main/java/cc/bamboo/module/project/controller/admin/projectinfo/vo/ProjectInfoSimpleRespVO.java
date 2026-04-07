package cc.bamboo.module.project.controller.admin.projectinfo.vo;

import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目核心表（基础+状态） Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectInfoSimpleRespVO {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18333")
    @ExcelProperty("项目ID")
    private Long projectId;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("项目名称")
    private String projectName;

    @Schema(description = "项目类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "项目类", converter = DictConvert.class)
    @DictFormat("biz_project_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer projectType;

    @Schema(description = "资产类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "资产类型", converter = DictConvert.class)
    @DictFormat("biz_asset_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer assetType;

    private Integer projectConfigType;

}