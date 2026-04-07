package cc.bamboo.module.chain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 链任务状态枚举
 *
 * @author Swolf
 */
@Getter
@AllArgsConstructor
public enum ChainTaskStatusEnum {

    /**
     * 待处理
     */
    PENDING(1, "待处理"),

    /**
     * 处理中
     */
    PROCESSING(2, "处理中"),

    /**
     * 成功
     */
    SUCCESS(3, "成功"),

    /**
     * 失败
     */
    FAILED(4, "失败"),

    /**
     * 已取消
     */
    CANCELLED(5, "已取消");

    /**
     * 状态
     */
    private final Integer status;

    /**
     * 描述
     */
    private final String description;

    /**
     * 根据状态获取枚举
     *
     * @param status 状态
     * @return 枚举
     */
    public static ChainTaskStatusEnum getByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (ChainTaskStatusEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }
}
