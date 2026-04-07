package cc.bamboo.module.user.service.auth;

import cc.bamboo.module.user.controller.app.userinfo.vo.*;

import javax.validation.Valid;

/**
 * 会员的认证 Service 接口
 *
 * 提供用户的账号密码登录、token 的校验等认证相关的功能
 *
 * @author 芋道源码
 */
public interface AuthService {

    /**
     * 手机 + 密码登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AppAuthLoginRespVO login(@Valid AppAuthLoginReqVO reqVO);

    /**
     * 基于 token 退出登录
     *
     * @param token token
     */
    void logout(String token);

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果
     */
    AppAuthLoginRespVO refreshToken(String refreshToken);

    /**
     * 手机 + 验证码登陆
     *
     * @param reqVO 登陆信息
     * @return 登录结果
     */
    AppAuthLoginRespVO smsLogin(@Valid AppAuthSmsLoginReqVO reqVO);

    /**
     * 给用户发送短信验证码
     *
     * @param userId 用户编号
     * @param reqVO  发送信息
     */
    void sendSmsCode(Long userId, AppAuthSmsSendReqVO reqVO);

    /**
     * 手机号 + 验证码 + 密码注册
     *
     * @param reqVO 注册信息
     * @return 登录结果
     */
    AppAuthLoginRespVO register(@Valid AppAuthRegisterReqVO reqVO);

    /**
     * 通过验证码重置密码
     *
     * @param reqVO 重置密码信息
     */
    void resetPassword(@Valid AppAuthResetPasswordReqVO reqVO);

    /**
     * 邮箱 + 密码登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AppAuthLoginRespVO emailLogin(@Valid AppAuthEmailLoginReqVO reqVO);

    /**
     * 邮箱 + 验证码登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AppAuthLoginRespVO emailCodeLogin(@Valid AppAuthEmailCodeLoginReqVO reqVO);

    /**
     * 发送邮箱验证码
     *
     * @param userId 用户编号
     * @param reqVO  发送信息
     */
    void sendEmailCode(Long userId, AppAuthEmailSendReqVO reqVO);

    /**
     * 邮箱 + 验证码 + 密码注册
     *
     * @param reqVO 注册信息
     * @return 登录结果
     */
    AppAuthLoginRespVO emailRegister(@Valid AppAuthEmailRegisterReqVO reqVO);

    /**
     * 通过邮箱验证码重置密码
     *
     * @param reqVO 重置密码信息
     */
    void emailResetPassword(@Valid AppAuthEmailResetPasswordReqVO reqVO);

    /**
     * 绑定手机号（需要手机验证码 + 邮箱验证码双重验证）
     *
     * @param userId 用户编号
     * @param reqVO  绑定信息
     */
    void bindMobile(Long userId, @Valid AppAuthBindMobileReqVO reqVO);

    /**
     * 绑定邮箱（需要手机验证码 + 邮箱验证码双重验证）
     *
     * @param userId 用户编号
     * @param reqVO  绑定信息
     */
    void bindEmail(Long userId, @Valid AppAuthBindEmailReqVO reqVO);

    /**
     * 初始化2FA绑定(生成密钥和二维码)
     *
     * @param userId 用户ID
     * @return 2FA密钥和二维码URL
     */
    AppAuthBind2FARespVO init2FABinding(Long userId);

    /**
     * 确认绑定2FA
     *
     * @param userId 用户ID
     * @param reqVO  绑定信息
     */
    void bind2FA(Long userId, @Valid AppAuthBind2FAReqVO reqVO);

    /**
     * 验证2FA验证码
     *
     * @param userId 用户ID
     * @param code   验证码
     * @return 是否验证通过
     */
    boolean verify2FA(Long userId, String code);

    /**
     * 设置用户默认币种
     *
     * @param userId 用户ID
     * @param reqVO  币种信息
     */
    void setDefaultCurrency(Long userId, @Valid AppAuthSetCurrencyReqVO reqVO);

    /**
     * 获取用户基础信息
     *
     * @param userId
     * @author: Hus
     * @date: 2026/1/9 10:59
     * @return: AppUserInfoRespVO
     * @description
     */
    AppUserInfoRespVO getUserInfo(Long userId);

    /**
     * 请求解绑2FA
     *
     */
    void requestUnbind2FA(AppAuthUnbind2FAReqVO reqVO);

    /**
     * 修改用户基础信息
     *
     * @param userId 用户ID
     * @param reqVO  修改信息
     */
    void updateProfile(Long userId, @Valid AppUserInfoUpdateReqVO reqVO);
}
