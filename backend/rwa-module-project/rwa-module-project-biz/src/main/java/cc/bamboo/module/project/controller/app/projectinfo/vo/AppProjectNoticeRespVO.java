package cc.bamboo.module.project.controller.app.projectinfo.vo;

import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目通告表（含全局通告） Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppProjectNoticeRespVO {

    @Schema(description = "通告ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11697")
    @ExcelProperty("通告ID")
    private Long id;

    @Schema(description = "通告编号（唯一，格式：NOTICE+日期+序号，如NOTICE202512200001）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("通告编号（唯一，格式：NOTICE+日期+序号，如NOTICE202512200001）")
    private String noticeNo;

    @Schema(description = "通告标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("通告标题")
    private String noticeTitle;

    @Schema(description = "通告内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("通告内容")
    private String noticeContent;

    @Schema(description = "附件URL（多个用逗号分隔，支持PDF/Word/图片等，如“url1,url2”）")
    @ExcelProperty("附件URL（多个用逗号分隔，支持PDF/Word/图片等，如“url1,url2”）")
    private String attachUrls;

    private LocalDateTime publishTime;


}