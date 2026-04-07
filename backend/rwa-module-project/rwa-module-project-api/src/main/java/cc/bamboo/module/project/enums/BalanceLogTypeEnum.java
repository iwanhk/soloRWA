package cc.bamboo.module.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 余额变动类型枚举
 *
 * @author Swolf
 */
@Getter
@AllArgsConstructor
public enum BalanceLogTypeEnum {

    /**
     * 1 - 购买成功（本金增加）
     */
    PURCHASE(1, "购买成功"),

    /**
     * 2 - 提前赎回（本金减少）
     */
    EARLY_REDEMPTION(2, "提前赎回"),

    /**
     * 3 - 到期赎回（本金减少）
     */
    MATURITY_REDEMPTION(3, "到期赎回"),

    /**
     * 4 - 分红发放（收益增加）
     */
    DIVIDEND_GRANT(4, "分红发放"),

    /**
     * 5 - 分红提取（收益减少）
     */
    DIVIDEND_WITHDRAW(5, "分红提取"),

    /**
     * 6 - 分红退还（收益增加，审核不通过）
     */
    DIVIDEND_REFUND(6, "分红退还"),

    /**
     * 7 - 提前赎回退还（本金增加，审核不通过）
     */
    EARLY_REDEMPTION_REFUND(7, "提前赎回退还"),

    /**
     * 8 - 到期赎回退还（本金增加，审核不通过）
     */
    MATURITY_REDEMPTION_REFUND(8, "到期赎回退还");

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 名称
     */
    private final String name;

}
