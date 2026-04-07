package cc.bamboo.module.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 账单审核状态枚举
 *
 * @author Swolf
 */
@Getter
@AllArgsConstructor
public enum AuditStatusEnum {

    PENDING_SUBMISSION(0,"待提交"),

    /**
     * 1 - 待审核
     */
    PENDING(1, "待审核"),

    /**
     * 2 - 审核通过
     */
    APPROVED(2, "审核通过"),

    /**
     * 3 - 审核不通过
     */
    REJECTED(3, "审核不通过"),

    PAY(4,"已支付")


    ;

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 状态名称
     */
    private final String name;

}
