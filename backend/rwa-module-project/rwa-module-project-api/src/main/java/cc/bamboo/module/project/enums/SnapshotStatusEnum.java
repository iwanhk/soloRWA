package cc.bamboo.module.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 项目快照状态枚举
 *
 * @author Swolf
 */
@Getter
@AllArgsConstructor
public enum SnapshotStatusEnum {

    /**
     * 1 - 待审核
     */
    PENDING(1, "待审核"),

    /**
     * 2 - 已通过
     */
    APPROVED(2, "已通过"),

    /**
     * 3 - 已拒绝
     */
    REJECTED(3, "已拒绝");

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 状态名称
     */
    private final String name;

}
