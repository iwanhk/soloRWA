package cc.bamboo.module.project.controller.app.projectorder.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 项目账单管理表 DO
 *
 * @author Swolf
 */
@Data
public class AppProjectBillDetailRespVO {

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

    private Long orderId;

    private String orderNo;
    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    private Long projectId;

    /**
     * 所属项目名称（冗余）
     */
    private String projectName;
    /**
     * 收款方（用户/企业名称）
     */
    private String payee;
    /**
     * 收款账户（银行卡号/链地址）
     */
    private String bankAccount;
    /**
     * 开户行（如“中国工商银行XX支行”）
     */
    private String bankName;
    /**
     * 账单金额（元）
     */
    private BigDecimal billAmount;
     /**
     * 实际到账金额（元）
     */
    private BigDecimal actualAmount;
    /**
     * 审核状态：1-待审核 2-审核通过 3-审核不通过
     *
     * 枚举 {@link TODO audit_status 对应的类}
     */
    private Integer auditStatus;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    /**
     * 审核备注（审核不通过原因）
     */
    private String auditRemark;


    private Integer quantity;

}