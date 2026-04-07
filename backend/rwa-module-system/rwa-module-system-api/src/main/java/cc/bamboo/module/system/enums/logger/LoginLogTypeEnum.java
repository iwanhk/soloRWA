package cc.bamboo.module.system.enums.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录日志的类型枚举
 */
@Getter
@AllArgsConstructor
public enum LoginLogTypeEnum {

    LOGIN_USERNAME(100), // 使用账号登录
    LOGIN_SOCIAL(101), // 使用社交登录
    LOGIN_PASSWORD(102), // 使用手机号+密码
    LOGIN_MOBILE(103), // 使用手机登陆
    LOGIN_SMS(104), // 使用短信登陆
    LOGIN_EMAIL_PASSWORD(105), // 使用邮箱+密码
    LOGIN_EMAIL(106), // 使用邮箱登陆

    LOGOUT_SELF(200),  // 自己主动登出
    LOGOUT_DELETE(202), // 强制退出
    ;

    /**
     * 日志类型
     */
    private final Integer type;

}
