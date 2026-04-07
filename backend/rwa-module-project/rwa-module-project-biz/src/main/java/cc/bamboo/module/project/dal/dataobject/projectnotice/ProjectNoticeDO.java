package cc.bamboo.module.project.dal.dataobject.projectnotice;

import cc.bamboo.framework.tenant.core.db.TenantBaseDO;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 项目通告表（含全局通告） DO
 *
 * @author Swolf
 */
@TableName("biz_project_notice")
@KeySequence("biz_project_notice_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectNoticeDO extends TenantBaseDO {

    /**
     * 通告ID
     */
    @TableId
    private Long id;
    /**
     * 通告编号（唯一，格式：NOTICE+日期+序号，如NOTICE202512200001）
     */
    private String noticeNo;
    /**
     * 关联项目ID（0表示全局通告，关联project_core.id）
     */
    private Long projectId;
    /**
     * 关联项目名称（冗余，0时为“全局通告”）
     */
    private String projectName;
    /**
     * 通告标题
     */
    private String noticeTitle;
    /**
     * 通告类型
     *
     * 枚举 {@link TODO biz_project_notice_type 对应的类}
     */
    private Integer noticeType;
    /**
     * 通告内容
     */
    private String noticeContent;
    /**
     * 附件URL（多个用逗号分隔，支持PDF/Word/图片等，如“url1,url2”）
     */
    private String attachUrls;
    /**
     * 发布人ID（关联sys_user.id）
     */
    private Long publishUserId;
    /**
     * 发布人名称（冗余）
     */
    private String publishUserName;
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    /**
     * 开始展示时间（NULL表示立即展示）
     */
    private LocalDateTime showStartTime;
    /**
     * 结束展示时间（NULL表示永久展示）
     */
    private LocalDateTime showEndTime;
    /**
     * 是否置顶：1-是 0-否（同一项目/全局仅1个置顶）
     */
    private Boolean isTop;
    /**
     * 通告状态：1-草稿 2-已发布 3-已下架
     *
     * 枚举 {@link TODO biz_project_notice_status 对应的类}
     */
    private Integer noticeStatus;
    /**
     * 阅读次数
     */
    private Integer readCount;
    /**
     * 是否弹窗展示：1-是 0-否（用户进入页面时弹窗）
     */
    private Boolean isPopup;
    /**
     * 备注（仅运营可见，如“临时通告，3天后下架”）
     */
    private String remark;


    private String noticeJson;

    private String language;
}