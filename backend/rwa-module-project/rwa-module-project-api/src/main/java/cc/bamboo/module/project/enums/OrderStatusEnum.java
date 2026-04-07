package cc.bamboo.module.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 *
 * @author Kiro
 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    PENDING_PAYMENT(0, "待支付"),
    UNDER_REVIEW(1, "审核中"),
    APPROVED(2, "审核通过"),
    REJECTED(3, "审核未通过"),
    CANCELLED(4, "已取消"),
    ENDED(5, "已赎回");
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
