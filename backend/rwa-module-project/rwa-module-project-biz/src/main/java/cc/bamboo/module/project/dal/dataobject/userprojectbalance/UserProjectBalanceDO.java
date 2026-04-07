package cc.bamboo.module.project.dal.dataobject.userprojectbalance;

import cc.bamboo.framework.tenant.core.db.TenantBaseDO;
import lombok.*;
import java.util.*;
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
 * 用户项目余额表（本金/收益汇总） DO
 *
 * @author Swolf
 */
@TableName("biz_user_project_balance")
@KeySequence("biz_user_project_balance_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProjectBalanceDO extends TenantBaseDO {

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
     * 项目ID（关联project_core.id）
     */
    private Long projectId;
    /**
     * 本金金额(元)（=申购份额×项目单价）
     */
    private BigDecimal principalAmount;
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
    /**
     * 余额状态：1-正常 2-已赎回 3-冻结
     */
    private Integer balanceStatus;

}