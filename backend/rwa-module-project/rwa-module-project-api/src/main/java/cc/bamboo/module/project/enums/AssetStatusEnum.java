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
public enum AssetStatusEnum {


    CONFIRMING(1, "确认中"),
    APPROVED(2, "审核通过"),
    WAIT(3, "待收益"),
    PROFIT(4, "收益中"),
    REDEEMABLE(5, "可赎回"),
    ENDED(6, "已结束"),
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
