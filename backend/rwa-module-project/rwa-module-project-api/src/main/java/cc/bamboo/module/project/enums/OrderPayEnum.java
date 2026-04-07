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
public enum OrderPayEnum {

    BANK_PAY("bank", "银行转账"),
    ;

    /**
     * 状态值
     */
    private final String type;

    /**
     * 状态名称
     */
    private final String name;

}
