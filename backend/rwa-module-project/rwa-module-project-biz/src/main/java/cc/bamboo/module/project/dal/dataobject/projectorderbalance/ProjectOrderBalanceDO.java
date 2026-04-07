package cc.bamboo.module.project.dal.dataobject.projectorderbalance;

import cc.bamboo.framework.tenant.core.db.TenantBaseDO;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户项目余额表 DO
 *
 * @author Swolf
 */
@TableName("biz_project_order_balance")
@KeySequence("biz_project_order_balance_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectOrderBalanceDO extends TenantBaseDO {

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
     * 本金金额(元)
     */
    private BigDecimal principalAmount;
    /**
     * 持有金额
     */
    private BigDecimal holdAmount;
    /**
     * 购买份额
     */
    private Integer buyQuantity;
    /**
     * 当前持有份额(份)（赎回后扣减）
     */
    private Integer holdQuantity;
    /**
     * 累计总收益(元)（含未提取）
     */
    private BigDecimal totalIncome;
    /**
     * 已提取分红(元)
     */
    private BigDecimal withdrawnDividend;
    /**
     * 冻结的分红(元)
     */
    private String freezeDividend;
    /**
     * 未提取分红(元)（=总收益-已提取）
     */
    private BigDecimal unwithdrawnDividend;
    /**
     * 累计赎回本金(元)（赎回时累加）
     */
    private BigDecimal totalRedemptionAmount;
    /**
     * 最后一次收益计算时间
     */
    private LocalDateTime lastIncomeCalcTime;
    /**
     * 最后一次分红提取时间
     */
    private LocalDateTime lastWithdrawTime;

    private String earningCurrency;

    private String investmentCurrency;

}