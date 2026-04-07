package cc.bamboo.module.user.dal.dataobject.noticeread;

import lombok.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 系统消息已读记录 DO
 *
 * @author Swolf
 */
@TableName("biz_notice_read")
@KeySequence("biz_notice_read_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeReadDO extends BaseDO {

    /**
     * 记录ID
     */
    @TableId
    private Long id;
    /**
     * 关联消息主表的系统消息ID
     */
    private Long noticeId;
    /**
     * 已读用户ID
     */
    private Long userId;
    /**
     * 阅读状态：0-未读 1-已读
     */
    private Integer readStatus;
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

}