package cc.bamboo.module.project.dal.dataobject.projectrevenue;

import lombok.*;

import java.time.LocalDate;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 项目收益 DO
 *
 * @author Swolf
 */
@TableName("biz_project_revenue")
@KeySequence("biz_project_revenue_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRevenueDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 收益日期
     */
    private LocalDate revenueDate;
    /**
     * 币种收益
     */
    private BigDecimal coinRevenue;
    /**
     * 汇率
     */
    private BigDecimal exchangeRate;
    /**
     * 算力
     */
    private BigDecimal computingPower;
    /**
     * 币种
     */
    private String coinCode;

    /**
     * 产品id
     */
    private Long projectId;
    /**
     * 产品名称
     */
    private String projectName;
    /**
     * 电力成本
     */
    private BigDecimal electricityCost;
    /**
     * 人力成本
     */
    private BigDecimal peopleCost;
    /**
     * 项目收益
     */
    private BigDecimal projectRevenue;
    /**
     * 可发放收益
     */
    private BigDecimal availableRevenue;
    /**
     * 是否发放收益
     */
    private Integer isSend;
    /**
     * 是否同步收益
     */
    private Integer isSync;
    /**
     * 备注
     */
    private String remark;

    private Integer quantity;

    private Integer chainStatus;

    private String chainHash;

}