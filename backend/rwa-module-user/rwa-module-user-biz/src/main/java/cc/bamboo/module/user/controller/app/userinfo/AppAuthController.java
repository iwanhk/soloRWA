package cc.bamboo.module.user.controller.app.userinfo;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.idempotent.core.annotation.Idempotent;
import cc.bamboo.framework.security.config.SecurityProperties;
import cc.bamboo.framework.security.core.util.SecurityFrameworkUtils;

import cc.bamboo.module.user.controller.app.userinfo.vo.*;
import cc.bamboo.module.user.enums.notice.NoticeTemplateEnum;
import cc.bamboo.module.user.service.auth.AuthService;
import cc.bamboo.module.user.service.noticemessage.NoticeMessageSendService;

import cc.bamboo.module.user.service.userbank.UserBankService;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static cc.bamboo.framework.common.pojo.CommonResult.success;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 基础")
@RestController
@RequestMapping("/user/auth")
@Validated
@Slf4j
public class AppAuthController {

    @Resource
    private AuthService authService;

    @Resource
    private UserBankService userBankService;

    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private NoticeMessageSendService noticeMessageSendService;

    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize maxFileSize;

    @Value("${rwa.sms-code.send-frequency}")
    private Duration smsSendFrequency;

    @Value("${rwa.sms-code.send-maximum-quantity-per-day}")
    private Integer smsSendMaximumQuantityPerDay;

    @PostMapping("/login")
    @Operation(summary = "使用账号 + 密码登录")
    @PermitAll
    public CommonResult<AppAuthLoginRespVO> login(@RequestBody @Valid AppAuthLoginReqVO reqVO) {
        return success(authService.login(reqVO));
    }

    @PostMapping("/logout")
    @Operation(summary = "登出系统")
    @PermitAll
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = SecurityFrameworkUtils.obtainAuthorization(request,
                securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StrUtil.isNotBlank(token)) {
            authService.logout(token);
        }
        return success(true);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新令牌")
    @Parameter(name = "refreshToken", description = "刷新令牌", required = true)
    @PermitAll
    public CommonResult<AppAuthLoginRespVO> refreshToken(@RequestBody @Valid RefreshTokenReqVO req) {
        return success(authService.refreshToken(req.getRefreshToken()));
    }

    @PostMapping("/sms-login")
    @Operation(summary = "使用手机 + 验证码登录")
    @PermitAll
    public CommonResult<AppAuthLoginRespVO> smsLogin(@RequestBody @Valid AppAuthSmsLoginReqVO reqVO) {
        return success(authService.smsLogin(reqVO));
    }

    @PostMapping("/send-sms-code")
    @Operation(summary = "发送手机验证码")
    @PermitAll
    public CommonResult<Boolean> sendSmsCode(@RequestBody @Valid AppAuthSmsSendReqVO reqVO) {
        authService.sendSmsCode(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/register")
    @Operation(summary = "使用手机号 + 验证码 + 密码注册")
    @PermitAll
    @Idempotent(timeout = 5, timeUnit = TimeUnit.SECONDS)
    public CommonResult<AppAuthLoginRespVO> register(@RequestBody @Valid AppAuthRegisterReqVO reqVO) {
        AppAuthLoginRespVO authLoginRespVO = authService.register(reqVO);
        // 注册成功发送消息
        noticeMessageSendService.sendSingleMessageAsync(authLoginRespVO.getUserId(),
                NoticeTemplateEnum.USER_REGISTER.getCode(), null);
        return success(authLoginRespVO);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "通过验证码重置密码")
    @PermitAll
    public CommonResult<Boolean> resetPassword(@RequestBody @Valid AppAuthResetPasswordReqVO reqVO) {
        authService.resetPassword(reqVO);
        return success(true);
    }

    // ========== 邮箱认证相关 ==========

    @PostMapping("/email-login")
    @Operation(summary = "使用邮箱 + 密码登录")
    @PermitAll
    public CommonResult<AppAuthLoginRespVO> emailLogin(@RequestBody @Valid AppAuthEmailLoginReqVO reqVO) {
        return success(authService.emailLogin(reqVO));
    }

    @PostMapping("/email-code-login")
    @Operation(summary = "使用邮箱 + 验证码登录")
    @PermitAll
    public CommonResult<AppAuthLoginRespVO> emailCodeLogin(@RequestBody @Valid AppAuthEmailCodeLoginReqVO reqVO) {
        return success(authService.emailCodeLogin(reqVO));
    }

    @PostMapping("/send-email-code")
    @Operation(summary = "发送邮箱验证码")
    @PermitAll
    public CommonResult<Boolean> sendEmailCode(@RequestBody @Valid AppAuthEmailSendReqVO reqVO) {
        authService.sendEmailCode(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/email-register")
    @Operation(summary = "使用邮箱 + 验证码 + 密码注册")
    @PermitAll
    @Idempotent(timeout = 5, timeUnit = TimeUnit.SECONDS)
    public CommonResult<AppAuthLoginRespVO> emailRegister(@RequestBody @Valid AppAuthEmailRegisterReqVO reqVO) {
        AppAuthLoginRespVO authLoginRespVO = authService.emailRegister(reqVO);
        // 注册成功发送消息
        noticeMessageSendService.sendSingleMessageAsync(authLoginRespVO.getUserId(),
                NoticeTemplateEnum.USER_REGISTER.getCode(), null);
        return success(authLoginRespVO);
    }

    @PostMapping("/email-reset-password")
    @Operation(summary = "通过邮箱验证码重置密码")
    @PermitAll
    public CommonResult<Boolean> emailResetPassword(@RequestBody @Valid AppAuthEmailResetPasswordReqVO reqVO) {
        authService.emailResetPassword(reqVO);
        return success(true);
    }

    // ========== 绑定手机号/邮箱 ==========

    @PostMapping("/bind-mobile")
    @Operation(summary = "绑定手机号（需要手机验证码 + 邮箱验证码双重验证）")
    public CommonResult<Boolean> bindMobile(@RequestBody @Valid AppAuthBindMobileReqVO reqVO) {
        authService.bindMobile(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/bind-email")
    @Operation(summary = "绑定邮箱（需要手机验证码 + 邮箱验证码双重验证）")
    public CommonResult<Boolean> bindEmail(@RequestBody @Valid AppAuthBindEmailReqVO reqVO) {
        authService.bindEmail(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/2fa/init")
    @Operation(summary = "初始化2FA绑定(生成密钥和二维码)")
    public CommonResult<AppAuthBind2FARespVO> init2FABinding() {
        return success(authService.init2FABinding(getLoginUserId()));
    }

    @PostMapping("/2fa/bind")
    @Operation(summary = "确认绑定2FA")
    public CommonResult<Boolean> bind2FA(@RequestBody @Valid AppAuthBind2FAReqVO reqVO) {
        authService.bind2FA(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/2fa/verify")
    @Operation(summary = "验证2FA验证码")
    public CommonResult<Boolean> verify2FA(@RequestBody @Valid AppAuthVerify2FAReqVO reqVO) {
        boolean verified = authService.verify2FA(getLoginUserId(), reqVO.getCode());
        return success(verified);
    }

    @PostMapping("/2fa/unbind-request")
    @Operation(summary = "请求解绑2FA")
    public CommonResult<Boolean> requestUnbind2FA(@RequestBody @Valid AppAuthUnbind2FAReqVO reqVO) {
        authService.requestUnbind2FA(reqVO);
        return success(true);
    }

    @PostMapping("/set-currency")
    @Operation(summary = "设置用户默认币种")
    public CommonResult<Boolean> setDefaultCurrency(@RequestBody @Valid AppAuthSetCurrencyReqVO reqVO) {
        authService.setDefaultCurrency(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/update-bank-info")
    @Operation(summary = "修改银行卡信息")
    public CommonResult<Boolean> updateBankInfo(@RequestBody @Valid AppUserBankUpdateReqVO reqVO) {
        userBankService.updateBankInfo(getLoginUserId(), reqVO);
        return success(true);
    }

    @GetMapping("/userinfo")
    @Operation(summary = "获取用户基础信息")
    public CommonResult<AppUserInfoRespVO> getUserAuthInfo() {
        return success(authService.getUserInfo(getLoginUserId()));
    }

    @PostMapping("/update-profile")
    @Operation(summary = "修改用户基础信息")
    public CommonResult<Boolean> updateProfile(@Valid AppUserInfoUpdateReqVO reqVO) {
        authService.updateProfile(getLoginUserId(), reqVO);
        return success(true);
    }

    @GetMapping("/config")
    @Operation(summary = "获取系统配置信息")
    @PermitAll
    public CommonResult<AppAuthConfigRespVO> getConfig() {
        return success(AppAuthConfigRespVO.builder()
                .maxFileSize(maxFileSize.toMegabytes())
                .smsSendFrequency(Math.toIntExact(smsSendFrequency.toMinutes()))
                .smsSendMaximumQuantityPerDay(smsSendMaximumQuantityPerDay)
                .build());
    }

}
