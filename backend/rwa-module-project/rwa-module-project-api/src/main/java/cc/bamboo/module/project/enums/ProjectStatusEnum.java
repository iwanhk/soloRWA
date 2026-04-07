package cc.bamboo.module.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 项目运行状态枚举
 * 用于管理项目的运行生命周期（计息状态）
 * 
 * 流程：未运行 -> 待运行审核 -> 运行中 -> 已结束
 * 
 * 注意：
 * - 封闭基金：开始运行后不能再购买
 * - 开放基金：运行中仍可购买
 * - 申请运行需要管理员审核，审核通过后第二天0点生效
 *
 * @author Kiro
 */
@Getter
@AllArgsConstructor
public enum ProjectStatusEnum {

    /**
     * 0 - 未运行（募资阶段）
     */
    NOT_RUNNING(0, "未运行"),

    /**
     * 1 - 待运行审核（已申请运行，等待管理员审核）
     */
    PENDING_RUN(1, "待运行审核"),

    /**
     * 2 - 运行中（计息中）
     */
    RUNNING(2, "运行中"),

    /**
     * 3 - 已结束
     */
    ENDED(3, "已结束");

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 根据状态值获取枚举
     */
    public static ProjectStatusEnum fromStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (ProjectStatusEnum e : values()) {
            if (e.getStatus().equals(status)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 是否正在运行
     */
    public boolean isRunning() {
        return this == RUNNING;
    }

    /**
     * 是否已结束
     */
    public boolean isEnded() {
        return this == ENDED;
    }
}
