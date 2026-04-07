package cc.bamboo.module.project.controller.admin.projectnotice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 项目通告表（含全局通告）新增/修改 Request VO")
@Data
public class ProjectNoticeUpdateReqVO {

    @Schema(description = "通告ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11697")
    private Long id;

    @Schema(description = "关联项目ID（0表示全局通告，关联project_core.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "27973")
    private Long projectId;


    @Schema(description = "通告标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String noticeTitle;


    @Schema(description = "通告内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String noticeContent;

    @Schema(description = "附件URL（多个用逗号分隔，支持PDF/Word/图片等，如“url1,url2”）")
    private String attachUrls;

    @Schema(description = "通告状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "通告状态不能为空")
    private Integer noticeStatus;

    private String noticeLanguage;

    private String language;

}