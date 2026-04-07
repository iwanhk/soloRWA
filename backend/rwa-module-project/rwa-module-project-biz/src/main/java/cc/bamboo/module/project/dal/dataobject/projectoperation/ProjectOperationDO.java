package cc.bamboo.module.project.dal.dataobject.projectoperation;

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
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 项目运营统计表 DO
 *
 * @author Swolf
 */
@TableName("biz_project_operation")
@KeySequence("biz_project_operation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectOperationDO extends TenantBaseDO {

    /**
     * 关联project_core.id
     */
    @TableId
    private Long projectId;
    /**
     * 参与投资人数
     */
    private Integer investorCount;
    /**
     * 申请分红人数
     */
    private Integer dividendApplyCount;
    /**
     * 总投资金额
     */
    private BigDecimal totalInvertor;
    /**
     * 申请分红金额
     */
    private BigDecimal dividendApplyAmount;
    /**
     * 提前赎回人数
     */
    private Integer earlyRedemptionPeople;
    /**
     * 提前赎回金额
     */
    private BigDecimal earlyRedemptionAmount;
    /**
     * 提前赎回份额
     */
    private Integer earlyRedemptionCount;
    /**
     * 到期赎回人数
     */
    private Integer maturityRedemptionCount;
    /**
     * 到期赎回金额
     */
    private BigDecimal maturityRedemptionAmount;
    /**
     * 投资人总收益
     */
    private BigDecimal totalInvestorIncome;
    /**
     * 投资人总收益率(%)
     */
    private BigDecimal totalInvestorYield;
    /**
     * 收益币种
     */
    private String earningCurrency;
    /**
     * 投资币种
     */
    private String investmentCurrency;


    private BigDecimal projectIncome;


}