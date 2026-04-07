package cc.bamboo.module.user.dal.dataobject.noticemessage;

import lombok.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户消息 DO
 *
 * @author Swolf
 */
@TableName("biz_notice_message")
@KeySequence("biz_notice_message_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeMessageDO extends BaseDO {

    /**
     * 用户ID
     */
    @TableId
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 消息类型
     *
     * 枚举 {@link TODO biz_notice_type 对应的类}
     */
    private Integer noticeType;
    /**
     * 模版编号
     */
    private Long templateId;
    /**
     * 模板编码
     */
    private String templateCode;

    private String templateTitle;
    /**
     * 模版发送人名称
     */
    private String templateNickname;
    /**
     * 模版内容
     */
    private String templateContent;
    /**
     * 模版类型
     */
    private Integer templateType;
    /**
     * 模版参数
     */
    private String templateParams;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 消息地址
     */
    private String noticeUrl;
    /**
     * 是否已读
     */
    private Boolean readStatus;
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

}