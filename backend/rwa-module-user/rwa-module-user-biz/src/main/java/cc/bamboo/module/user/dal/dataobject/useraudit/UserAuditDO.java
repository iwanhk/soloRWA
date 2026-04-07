package cc.bamboo.module.user.dal.dataobject.useraudit;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户投资者认证审核 DO
 *
 * @author Swolf
 */
@TableName("biz_user_audit")
@KeySequence("biz_user_audit_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuditDO extends BaseDO {

    /**
     * 审核记录ID
     */
    @TableId
    private Long id;
    /**
     * 关联用户ID
     */
    private Long userId;
    /**
     * 用户姓名（提交审核时的姓名）
     */
    private String realName;
    /**
     * 证件号（脱敏存储）
     */
    private String idCard;
    /**
     * 证件号有效期
     */
    private String idCardExpire;
    /**
     * 证件号人像面图片地址
     */
    private String idCardFrontUrl;
    /**
     * 证件号国徽面图片地址
     */
    private String idCardBackUrl;
    /**
     * 投资资质图片地址
     */
    private String investmentQualificationUrl;
    /**
     * 银行流水单图片地址
     */
    private String bankFlowUrl;
    /**
     * 住址证明图片地址
     */
    private String residenceProofUrl;
    /**
     * 关联用户银行卡ID（user_bank_card.id）
     */
    private Long bankCardId;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 审核状态：0-待提交 1-待审核 2-审核通过 3-审核驳回
     */
    private Integer auditStatus;
    /**
     * 提交版本（用户第N次提交认证）
     */
    private Integer submitVersion;
    /**
     * 审核备注（驳回原因）
     */
    private String auditRemark;
    /**
     * 是否为最新提交记录（1-是 0-否）
     */
    private Boolean isLatest;

}