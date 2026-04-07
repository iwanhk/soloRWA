package cc.bamboo.module.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 账单类型枚举
 *
 * @author Swolf
 */
@Getter
@AllArgsConstructor
public enum BillTypeEnum {

    DIVIDEND(1, "分红"),
    MATURITY_REDEMPTION(2, "到期赎回"),
    EARLY_REDEMPTION(3, "提前赎回");

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名称
     */
    private final String name;

}
