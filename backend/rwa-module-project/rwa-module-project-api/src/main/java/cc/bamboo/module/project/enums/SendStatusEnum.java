package cc.bamboo.module.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/1/7 18:30
 * @description
 */


@Getter
@AllArgsConstructor
public enum SendStatusEnum {

    NOT_SEND(0, "未发送"),
    SEND(1, "已发送"),
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
