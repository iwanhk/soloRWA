package cc.bamboo.module.project.dal.dataobject.projectfundconfig;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 基金项目配置 DO
 *
 * @author Swolf
 */
@TableName("biz_project_fund_config")
@KeySequence("biz_project_fund_config_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFundConfigDO extends BaseDO {

    /**
     * 项目ID(主键)
     */
    @TableId
    private Long projectId;
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
    /**
     * 基金公司配置
     */
    private String fundJson;

}