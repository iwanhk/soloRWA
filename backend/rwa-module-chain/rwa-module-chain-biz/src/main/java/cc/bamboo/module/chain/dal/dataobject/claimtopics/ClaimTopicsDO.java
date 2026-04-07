package cc.bamboo.module.chain.dal.dataobject.claimtopics;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 声明主题 DO
 *
 * @author Swolf
 */
@TableName("biz_claim_topics")
@KeySequence("biz_claim_topics_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimTopicsDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 主题名称
     */
    private String name;
    /**
     * 主题值
     */
    private String value;
    /**
     * 主题哈希
     */
    private String topic;

}