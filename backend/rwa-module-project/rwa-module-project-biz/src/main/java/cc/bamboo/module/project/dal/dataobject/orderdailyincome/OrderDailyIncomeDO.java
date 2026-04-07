package cc.bamboo.module.project.dal.dataobject.orderdailyincome;

import cc.bamboo.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 订单每日收益统计 DO
 *
 * @author Swolf
 */
@TableName("biz_order_daily_income")
@KeySequence("biz_order_daily_income_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDailyIncomeDO extends TenantBaseDO {

    /**
     * 记录ID
     */
    @TableId
    private Long id;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 项目ID
     */
    private Long projectId;
    /**
     * 收益日期
     */
    private LocalDate incomeDate;
    /**
     * 收益id
     */
    private Long projectRevenueId;
    /**
     * 当日持有份额
     */
    private Integer holdQuantity;
    /**
     * 收益发放时间
     */
    private LocalDateTime issueTime;
    /**
     * 当日收益
     */
    private BigDecimal dailyIncome;
    /**
     * 当日收益率
     */
    private BigDecimal incomeRate;
    /**
     * 累计收益
     */
    private BigDecimal cumulativeIncome;
    /**
     * 收益状态
     *
     * 枚举
     */
    private Integer status;

    private String coinCode;

    private String chainHash;

    private String address;
}