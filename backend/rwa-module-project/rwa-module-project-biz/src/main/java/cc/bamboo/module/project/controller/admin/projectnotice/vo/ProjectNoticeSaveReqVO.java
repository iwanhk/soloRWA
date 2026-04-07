package cc.bamboo.module.project.controller.admin.projectnotice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目通告表（含全局通告）新增/修改 Request VO")
@Data
public class ProjectNoticeSaveReqVO {

    @Schema(description = "通告ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11697")
    private Long id;

 /*   @Schema(description = "通告编号（唯一，格式：NOTICE+日期+序号，如NOTICE202512200001）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "通告编号（唯一，格式：NOTICE+日期+序号，如NOTICE202512200001）不能为空")
    private String noticeNo;*/

    @Schema(description = "关联项目ID（0表示全局通告，关联project_core.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "27973")
    @NotNull(message = "项目ID")
    private Long projectId;

/*    @Schema(description = "关联项目名称（冗余，0时为“全局通告”）", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "关联项目名称（冗余，0时为“全局通告”）不能为空")
    private String projectName;*/

    @Schema(description = "通告标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "通告标题不能为空")
    private String noticeTitle;

   /* @Schema(description = "通告类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "通告类型不能为空")
    private Integer noticeType;*/

    @Schema(description = "通告内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "通告内容不能为空")
    private String noticeContent;

    @Schema(description = "附件URL（多个用逗号分隔，支持PDF/Word/图片等，如“url1,url2”）")
    private String attachUrls;

    @Schema(description = "通告状态：1-草稿 2-已发布 3-已下架", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "通告状态：1-草稿 2-已发布 3-已下架不能为空")
    private Integer noticeStatus;

    /*@Schema(description = "发布人ID（关联sys_user.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "32458")
    @NotNull(message = "发布人ID（关联sys_user.id）不能为空")
    private Long publishUserId;

    @Schema(description = "发布人名称（冗余）", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "发布人名称（冗余）不能为空")
    private String publishUserName;

    @Schema(description = "发布时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "发布时间不能为空")
    private LocalDateTime publishTime;

    @Schema(description = "开始展示时间（NULL表示立即展示）")
    private LocalDateTime showStartTime;

    @Schema(description = "结束展示时间（NULL表示永久展示）")
    private LocalDateTime showEndTime;

    @Schema(description = "是否置顶：1-是 0-否（同一项目/全局仅1个置顶）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否置顶：1-是 0-否（同一项目/全局仅1个置顶）不能为空")
    private Boolean isTop;



    @Schema(description = "阅读次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "31688")
    @NotNull(message = "阅读次数不能为空")
    private Integer readCount;

    @Schema(description = "是否弹窗展示：1-是 0-否（用户进入页面时弹窗）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否弹窗展示：1-是 0-否（用户进入页面时弹窗）不能为空")
    private Boolean isPopup;

    @Schema(description = "备注（仅运营可见，如“临时通告，3天后下架”）", example = "随便")
    private String remark;*/

    private String noticeJson;

    private String language;

}