package cc.bamboo.module.project.dal.dataobject.projectminingconfig;

import cc.bamboo.framework.tenant.core.db.TenantBaseDO;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 挖矿项目配置 DO
 *
 * @author swolf
 */
@TableName("biz_project_mining_config")
@KeySequence("biz_project_mining_config_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMiningConfigDO extends TenantBaseDO {

    /**
     * 项目ID(主键)
     */
    @TableId
    private Long projectId;

    /**
     * 矿池AccessKey(加密)
     */
    private String poolAccessKey;
    /**
     * 矿池私钥(加密)
     */
    private String poolPrivateKey;
    /**
     * 矿池子账号
     */
    private String poolName;

    /**
     * 单T功耗(W)
     */
    private BigDecimal powerConsumption;
    /**
     * 电价(元/度)
     */
    private BigDecimal electricityPrice;
    /**
     * 月运维成本
     */
    private BigDecimal operationCost;
    /**
     * 运维成本类型:0-固定值/月 1-按天
     */
    private Integer operationCostType;
    /**
     * 团队分成比例(%)
     */
    private BigDecimal teamShareRatio;
    /**
     * 最低收益阈值(日)
     */
    private BigDecimal thresholdMin;
    /**
     * 最高收益阈值(日)
     */
    private BigDecimal thresholdMax;
    /**
     * 阈值预警:0-关 1-开
     */
    private Integer alertEnabled;

}