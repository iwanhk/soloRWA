package cc.bamboo.module.project.dal.dataobject.projectorderbalancelog;

import cc.bamboo.framework.tenant.core.db.TenantBaseDO;
import lombok.*;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户项目余额记录 DO
 *
 * @author Swolf
 */
@TableName("biz_project_order_balance_log")
@KeySequence("biz_project_order_balance_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectOrderBalanceLogDO extends TenantBaseDO {

    /**
     * 记录ID
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 项目ID
     */
    private Long projectId;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 操作金额
     */
    private BigDecimal amount;
    /**
     * 操作后金额
     */
    private BigDecimal afterAmount;
    /**
     * 类型
     *
     * 枚举 {@link cc.bamboo.module.project.enums.BalanceLogTypeEnum}
     */
    private Integer type;

    private String coinCode;

}