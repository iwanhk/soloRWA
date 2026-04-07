package cc.bamboo.module.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Hus
 * @version 1.0
 * @date 2025/12/17 16:44
 * @description
 */
@Getter
@AllArgsConstructor
public enum AppLoginResultEnum {

    SUCCESS(0, "成功"), // 成功
    BAD_CREDENTIALS(10, "账号或密码不正确"), // 账号或密码不正确
    USER_DISABLED(20, "用户被禁用"), // 用户被禁用
    CAPTCHA_NOT_FOUND(30, "图片验证码不存在"), // 图片验证码不存在
    CAPTCHA_CODE_ERROR(31, "图片验证码不正确"), // 图片验证码不正确

    ;

    /**
     * 结果
     */
    private final Integer result;

    private final String message;
}
