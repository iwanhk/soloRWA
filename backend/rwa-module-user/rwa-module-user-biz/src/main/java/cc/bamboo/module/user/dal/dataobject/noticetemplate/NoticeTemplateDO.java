package cc.bamboo.module.user.dal.dataobject.noticetemplate;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 消息模板 DO
 *
 * @author Swolf
 */
@TableName("biz_notice_template")
@KeySequence("biz_notice_template_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeTemplateDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 模版编码
     */
    private String code;
    /**
     * 发送人名称
     */
    private String nickname;
    /**
     * 模版内容
     */
    private String content;

    /**
     * 标题
     */
    private String title;
    /**
     * 类型
     *
     * 枚举 {@link TODO biz_notice_type 对应的类}
     */
    private Integer type;
    /**
     * 参数数组
     */
    private String params;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}