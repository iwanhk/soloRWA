package cc.bamboo.module.project.dal.dataobject.projectbill;

import cc.bamboo.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

/**
 * 项目账单管理表 DO
 *
 * @author Swolf
 */
@TableName("biz_project_bill")
@KeySequence("biz_project_bill_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectBillDO extends TenantBaseDO {

    /**
     * 账单ID
     */
    @TableId
    private Long id;
    /**
     * 流水号（唯一，如：BILL202512160001）
     */
    private String billNo;
    /**
     * 账单类型：1-分红 2-到期赎回 3-提前赎回
     */
    private Integer billType;
    /**
     * 申请时间
     */
    private LocalDateTime applyTime;
    /**
     * 申请用户ID（关联用户表）
     */
    private Long userId;
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 项目ID（关联project_core.id）
     */
    private Long projectId;
    /**
     * 所属项目名称（冗余）
     */
    private String projectName;
    /**
     * 收款方（用户/企业名称）
     */
    private String bankAccountName;
    /**
     * 收款账户（银行卡号/链地址）
     */
    private String bankAccount;
    /**
     * 开户行（如“中国工商银行XX支行”）
     */
    private String bankName;
    /**
     * 申请数量
     */
    private Integer quantity;
    /**
     * 申请金额
     */
    private BigDecimal billAmount;
    /**
     * 申请币种
     */
    private String billCoin;
    /**
     * 审核状态：1-待审核 2-审核通过 3-审核不通过 4已支付
     */
    private Integer auditStatus;
    /**
     * 到账日
     */
    private Integer arrivalDay;
    /**
     * 实际到账
     */
    private BigDecimal actualAmount;
    /**
     * 到账币种
     */
    private String actualCoin;
    /**
     * 到账汇率
     */
    private BigDecimal actualExchangeRate;
    /**
     * 审核人ID
     */
    private Long auditUserId;
    /**
     * 审核人名称
     */
    private String auditUserName;
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    /**
     * 审核备注（审核不通过原因）
     */
    private String auditRemark;
    /**
     * 支付凭证URL（审核通过后上传）
     */
    private String payVoucherUrl;
    /**
     * 支付时间（凭证上传时记录）
     */
    private LocalDateTime payTime;

    private String commissionRate;
}