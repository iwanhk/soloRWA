package cc.bamboo.module.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 链状态枚举
 *
 * @author Swolf
 */
@Getter
@AllArgsConstructor
public enum ChainStatusEnum {

    /**
     * 未处理
     */
    UNPROCESSED(-1, "未处理"),

    /**
     * 待处理
     */
    PENDING(0, "待处理"),

    /**
     * 成功
     */
    SUCCESS(1, "成功"),

    /**
     * 失败
     */
    FAILED(2, "失败");

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 状态名称
     */
    private final String name;

}
