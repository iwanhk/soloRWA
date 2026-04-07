package cc.bamboo.module.user.dal.dataobject.userbank;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户银行卡信息 DO
 *
 * @author Swolf
 */
@TableName("biz_user_bank")
@KeySequence("biz_user_bank_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBankDO extends BaseDO {

    /**
     * 银行卡记录ID
     */
    @TableId
    private Long id;
    /**
     * 关联用户ID
     */
    private Long userId;
    /**
     * 银行卡开户名（需与实名一致）
     */
    private String bankAccountName;
    /**
     * 银行卡号
     */
    private String bankAccount;
    /**
     * 开户行
     */
    private String bankName;
    /**
     * 开户行支行
     */
    private String bankBranch;
    /**
     * 是否默认银行卡
     */
    private Boolean isDefault;
    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 审核备注
     */
    private String auditRemark;

    private String oldBank;

}