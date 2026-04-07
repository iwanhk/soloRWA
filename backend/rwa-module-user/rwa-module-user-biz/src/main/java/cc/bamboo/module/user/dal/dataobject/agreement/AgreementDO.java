package cc.bamboo.module.user.dal.dataobject.agreement;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 系统协议表 DO
 *
 * @author Swolf
 */
@TableName("biz_agreement")
@KeySequence("biz_agreement_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgreementDO extends BaseDO {

    /**
     * 协议ID
     */
    @TableId
    private Long id;
    /**
     * 协议类型
     *
     * 枚举 {@link TODO agreement_type 对应的类}
     */
    private Integer agreementType;

    private String agreementKey;
    /**
     * 协议标题
     */
    private String agreementTitle;
    /**
     * 协议内容
     */
    private String agreementContent;
    /**
     * 协议版本号
     */
    private String version;
    /**
     * 是否当前生效版本：1-是 0-否（同一类型仅1个生效版本）
     */
    private Boolean isCurrent;
    /**
     * 生效时间
     */
    private LocalDateTime effectiveTime;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

}