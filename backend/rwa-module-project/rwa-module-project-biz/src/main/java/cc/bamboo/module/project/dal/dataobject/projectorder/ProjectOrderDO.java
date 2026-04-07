package cc.bamboo.module.project.dal.dataobject.projectorder;

import cc.bamboo.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 项目认购订单 DO
 *
 * @author Swolf
 */
@TableName("biz_project_order")
@KeySequence("biz_project_order_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectOrderDO extends TenantBaseDO {

    /**
     * 订单ID
     */
    @TableId
    private Long id;
    /**
     * 订单号（唯一，如：ORD202512160001）
     */
    private String orderNo;
    /**
     * 申请日期
     */
    private LocalDateTime applyDate;
    /**
     * 用户ID（关联用户表）
     */
    private Long userId;
    /**
     * 订单状态：1-待支付 2-审核中 3-审核通过 4-审核未通过 5-已取消
     */
    private Integer orderStatus;
    /**
     * 项目ID（关联project_core.id）
     */
    private Long projectId;
    /**
     * 项目名称（冗余）
     */
    private String projectName;
    /**
     * 申购份额(份)
     */
    private Integer subscribeQuantity;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 金额(元)（=申购份额×项目发行单价）
     */
    private BigDecimal totalAmount;
    /**
     * 购买确认时间
     */
    private LocalDateTime confirmPurchaseTime;
    /**
     * 支付方式
     */
    private String payType;
    /**
     * 链地址
     */
    private String chainAddress;
    /**
     * 合同号
     */
    private String contractNo;
    /**
     * 支付凭证图片URL
     */
    private String payVoucherUrl;
    /**
     * 审核时间（未审核为空）
     */
    private LocalDateTime auditTime;
    /**
     * 审核人ID（关联用户表）
     */
    private Long auditUserId;
    /**
     * 审核人名称
     */
    private String auditUserName;
    /**
     * 链验证状态
     */
    private Integer chainStatus;
    /**
     * 审核备注（如审核未通过原因）
     */
    private String auditRemark;
    /**
     * 取消时间（已取消订单填充）
     */
    private LocalDateTime cancelTime;
    /**
     * 取消原因（如超时未支付）
     */
    private String cancelReason;
    /**
     * 订单过期时间（待支付订单超时时间，如创建后24小时）
     */
    private LocalDateTime expireTime;

    private String payBank;

    private String earningCurrency;

    private String investmentCurrency;

    private String auditFiles;

    /**
     * 锁定期-开始
     */
    private LocalDate lockStartTime;
    /**
     * 锁定期-结束
     */
    private LocalDate lockEndTime;

    /**
     * 项目配置类型 0 挖矿 1 基金
     */
    private Integer projectConfigType;

}