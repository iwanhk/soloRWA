package cc.bamboo.module.project.controller.app.projectinfo.vo;

import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 用户银行卡信息 DO
 *
 * @author Swolf
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AppUserBankRespVO{

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

    private Integer auditStatus;

}