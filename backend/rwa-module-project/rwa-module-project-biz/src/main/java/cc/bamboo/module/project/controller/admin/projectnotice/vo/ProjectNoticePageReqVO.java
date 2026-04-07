package cc.bamboo.module.project.controller.admin.projectnotice.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 项目通告表（含全局通告）分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectNoticePageReqVO extends PageParam {

    @Schema(description = "通告编号（唯一，格式：NOTICE+日期+序号，如NOTICE202512200001）")
    private String noticeNo;

    @Schema(description = "关联项目ID（0表示全局通告，关联project_core.id）", example = "27973")
    private Long projectId;

    @Schema(description = "关联项目名称（冗余，0时为“全局通告”）", example = "张三")
    private String projectName;

    @Schema(description = "通告标题")
    private String noticeTitle;

    @Schema(description = "通告类型", example = "1")
    private Integer noticeType;

    @Schema(description = "通告内容")
    private String noticeContent;

    @Schema(description = "附件URL（多个用逗号分隔，支持PDF/Word/图片等，如“url1,url2”）")
    private String attachUrls;

    @Schema(description = "发布人ID（关联sys_user.id）", example = "32458")
    private Long publishUserId;

    @Schema(description = "发布人名称（冗余）", example = "李四")
    private String publishUserName;

    @Schema(description = "发布时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] publishTime;

    @Schema(description = "开始展示时间（NULL表示立即展示）")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] showStartTime;

    @Schema(description = "结束展示时间（NULL表示永久展示）")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] showEndTime;

    @Schema(description = "是否置顶：1-是 0-否（同一项目/全局仅1个置顶）")
    private Boolean isTop;

    @Schema(description = "通告状态：1-草稿 2-已发布 3-已下架", example = "2")
    private Integer noticeStatus;

    @Schema(description = "阅读次数", example = "31688")
    private Integer readCount;

    @Schema(description = "是否弹窗展示：1-是 0-否（用户进入页面时弹窗）")
    private Boolean isPopup;

    @Schema(description = "备注（仅运营可见，如“临时通告，3天后下架”）", example = "随便")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}