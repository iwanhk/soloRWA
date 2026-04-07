package cc.bamboo.module.project.controller.admin.projectnotice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 项目通告表（含全局通告） Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectNoticeRespVO {

    @Schema(description = "通告ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11697")
    @ExcelProperty("通告ID")
    private Long id;

    @Schema(description = "通告编号（唯一，格式：NOTICE+日期+序号，如NOTICE202512200001）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("通告编号（唯一，格式：NOTICE+日期+序号，如NOTICE202512200001）")
    private String noticeNo;

    @Schema(description = "关联项目ID（0表示全局通告，关联project_core.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "27973")
    @ExcelProperty("关联项目ID（0表示全局通告，关联project_core.id）")
    private Long projectId;

    @Schema(description = "关联项目名称（冗余，0时为“全局通告”）", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("关联项目名称（冗余，0时为“全局通告”）")
    private String projectName;

    @Schema(description = "通告标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("通告标题")
    private String noticeTitle;

    @Schema(description = "通告类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "通告类型", converter = DictConvert.class)
    @DictFormat("biz_project_notice_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer noticeType;

    @Schema(description = "通告内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("通告内容")
    private String noticeContent;

    @Schema(description = "附件URL（多个用逗号分隔，支持PDF/Word/图片等，如“url1,url2”）")
    @ExcelProperty("附件URL（多个用逗号分隔，支持PDF/Word/图片等，如“url1,url2”）")
    private String attachUrls;

    @Schema(description = "发布人ID（关联sys_user.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "32458")
    @ExcelProperty("发布人ID（关联sys_user.id）")
    private Long publishUserId;

    @Schema(description = "发布人名称（冗余）", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("发布人名称（冗余）")
    private String publishUserName;

    @Schema(description = "发布时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "开始展示时间（NULL表示立即展示）")
    @ExcelProperty("开始展示时间（NULL表示立即展示）")
    private LocalDateTime showStartTime;

    @Schema(description = "结束展示时间（NULL表示永久展示）")
    @ExcelProperty("结束展示时间（NULL表示永久展示）")
    private LocalDateTime showEndTime;

    @Schema(description = "是否置顶：1-是 0-否（同一项目/全局仅1个置顶）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否置顶：1-是 0-否（同一项目/全局仅1个置顶）")
    private Boolean isTop;

    @Schema(description = "通告状态：1-草稿 2-已发布 3-已下架", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "通告状态：1-草稿 2-已发布 3-已下架", converter = DictConvert.class)
    @DictFormat("biz_project_notice_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer noticeStatus;

    @Schema(description = "阅读次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "31688")
    @ExcelProperty("阅读次数")
    private Integer readCount;

    @Schema(description = "是否弹窗展示：1-是 0-否（用户进入页面时弹窗）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否弹窗展示：1-是 0-否（用户进入页面时弹窗）")
    private Boolean isPopup;

    @Schema(description = "备注（仅运营可见，如“临时通告，3天后下架”）", example = "随便")
    @ExcelProperty("备注（仅运营可见，如“临时通告，3天后下架”）")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    private String noticeJson;

    private String language;

}