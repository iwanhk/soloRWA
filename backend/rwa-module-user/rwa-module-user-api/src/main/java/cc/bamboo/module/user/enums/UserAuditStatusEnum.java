package cc.bamboo.module.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 账单审核状态枚举
 *
 * @author Swolf
 */
@Getter
@AllArgsConstructor
public enum UserAuditStatusEnum {

    /**
     * 0 - 未审核
     */
    NOT_AUDITED(0, "未审核"),

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
    REJECTED(3, "审核不通过");

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 状态名称
     */
    private final String name;

}
