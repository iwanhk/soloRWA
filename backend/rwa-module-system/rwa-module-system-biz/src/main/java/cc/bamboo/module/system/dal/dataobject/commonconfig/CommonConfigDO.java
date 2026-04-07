package cc.bamboo.module.system.dal.dataobject.commonconfig;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 参数配置 DO
 *
 * @author swolf
 */
@TableName("biz_common_config")
@KeySequence("biz_common_config_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonConfigDO extends BaseDO {

    /**
     * 参数主键
     */
    @TableId
    private Long id;
    /**
     * 参数名称
     */
    private String name;
    /**
     * 参数键名
     */
    private String configKey;
    /**
     * 参数键值
     */
    private String value;
    /**
     * 前端可用
     */
    private Integer isApp;
    /**
     * 备注
     */
    private String remark;

}