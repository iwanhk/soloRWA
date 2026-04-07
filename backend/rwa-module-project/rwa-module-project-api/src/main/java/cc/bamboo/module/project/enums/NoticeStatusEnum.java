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
public enum NoticeStatusEnum {

    DRAFT(1, "草稿"),
    RELEASED(2, "已发布"),
    WITHDRAW(3, "撤回");

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名称
     */
    private final String name;

}
