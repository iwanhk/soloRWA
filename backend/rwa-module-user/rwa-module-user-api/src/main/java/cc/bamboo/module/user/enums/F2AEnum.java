package cc.bamboo.module.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/1/9 10:51
 * @description
 */
@Getter
@AllArgsConstructor
public enum F2AEnum {

    NOT_ENABLED(0, "未开启"),


    ENABLED(1, "已开启"),


    PENDING_VERIFICATION(2, "待验证"),

    //解绑中
    UNBINDING(3,"解绑中")

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
