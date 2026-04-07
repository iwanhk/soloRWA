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
public enum ChainAddressStatusEnum {


    BINDING(0, "绑定中"),


    SUCCESS(1, "正常"),


    UNBINDING(2, "解绑中"),


    UNBIND(3, "已解绑"),

    BIND_FAIL(4, "绑定失败"),
    UNBIND_FAIL(5, "解绑失败"),
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
