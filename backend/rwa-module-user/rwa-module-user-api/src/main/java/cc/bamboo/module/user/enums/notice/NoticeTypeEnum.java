package cc.bamboo.module.user.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 *
 * @author Kiro
 */
@Getter
@AllArgsConstructor
public enum NoticeTypeEnum {

    SYSTEM(1, "系统消息"),
    PERSONAL(2, "个人消息"),
;

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名称
     */
    private final String name;

}
