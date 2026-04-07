package cc.bamboo.module.user.service.auth;

import cc.bamboo.framework.common.enums.CommonStatusEnum;
import cc.bamboo.framework.common.enums.UserTypeEnum;
import cc.bamboo.framework.common.util.monitor.TracerUtils;
import cc.bamboo.framework.common.util.servlet.ServletUtils;
import cc.bamboo.framework.redis.service.RedisService;
import cc.bamboo.module.project.enums.AuditStatusEnum;
import cc.bamboo.module.system.api.logger.LoginLogApi;
import cc.bamboo.module.system.api.logger.dto.LoginLogCreateReqDTO;
import cc.bamboo.module.system.api.oauth2.OAuth2TokenApi;
import cc.bamboo.module.system.api.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import cc.bamboo.module.system.api.oauth2.dto.OAuth2AccessTokenRespDTO;
import cc.bamboo.module.system.api.mail.MailSendApi;
import cc.bamboo.module.system.api.mail.dto.MailCodeSendReqDTO;
import cc.bamboo.module.system.api.mail.dto.MailCodeUseReqDTO;
import cc.bamboo.module.system.api.sms.SmsCodeApi;
import cc.bamboo.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cc.bamboo.module.system.enums.logger.LoginLogTypeEnum;
import cc.bamboo.module.system.enums.logger.LoginResultEnum;
import cc.bamboo.module.system.enums.oauth2.OAuth2ClientConstants;
import cc.bamboo.module.system.enums.sms.SmsSceneEnum;
import cc.bamboo.module.user.controller.admin.userinfo.vo.UserInfoSaveReqVO;
import cc.bamboo.module.user.controller.admin.userloginlog.vo.UserLoginLogSaveReqVO;
import cc.bamboo.module.user.controller.app.userinfo.vo.*;
import cc.bamboo.module.user.dal.dataobject.userchain.UserChainDO;
import cc.bamboo.module.user.dal.mysql.userchain.UserChainMapper;
import cc.bamboo.module.user.dal.mysql.userinfo.UserInfoMapper;
import cc.bamboo.module.user.enums.ChainAddressStatusEnum;
import cc.bamboo.module.user.enums.F2AEnum;
import cc.bamboo.module.user.util.IpToCityByLocalDB;
import cc.bamboo.module.user.util.TwoFactorAuthUtils;
import cc.bamboo.module.user.convert.auth.AuthConvert;
import cc.bamboo.module.user.dal.dataobject.userinfo.UserInfoDO;
import cc.bamboo.module.user.enums.AppLoginResultEnum;
import cc.bamboo.module.user.service.userinfo.UserInfoService;
import cc.bamboo.module.user.service.userloginlog.UserLoginLogService;
import cc.bamboo.module.infra.api.file.FileApi;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.framework.common.util.servlet.ServletUtils.getClientIP;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

import static cc.bamboo.module.user.enums.ErrorCodeConstants.*;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.AUTH_LOGIN_USER_DISABLED;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.F2A_NOT_INIT;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.F2A_VERIFY_CODE_ERROR;

/**
 * 会员的认证 Service 接口
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserInfoService userService;

    @Resource
    private LoginLogApi loginLogApi;

    @Resource
    private OAuth2TokenApi oauth2TokenApi;

    @Resource
    private UserLoginLogService userLoginLogService;

    @Resource
    private SmsCodeApi smsCodeApi;

    @Resource
    private MailSendApi mailSendApi;

    @Resource
    private RedisService redisService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserChainMapper userChainMapper;

    @Resource
    private FileApi fileApi;

    @Override
    public AppAuthLoginRespVO login(AppAuthLoginReqVO reqVO) {
        // 使用手机 + 密码，进行登录。
        UserInfoDO user = login0(reqVO.getMobile(), reqVO.getPassword());

        // 创建 Token 令牌，记录登录日志
        AppAuthLoginRespVO appAuthLoginRespVO = createTokenAfterLoginSuccess(user, LoginLogTypeEnum.LOGIN_PASSWORD);

        // 判断是否过期
        return appAuthLoginRespVO;
    }

    private UserInfoDO login0(String mobile, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_PASSWORD;
        // 校验账号是否存在
        UserInfoDO user = userService.getUserInfoByMobile(mobile);
        if (user == null) {
            createLoginLog(null, logTypeEnum, AppLoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!userService.isPasswordMatch(password, user.getPassword())) {
            createLoginLog(user.getId(), logTypeEnum, AppLoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 校验是否禁用
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            createLoginLog(user.getId(), logTypeEnum, AppLoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    private AppAuthLoginRespVO createTokenAfterLoginSuccess(UserInfoDO user, LoginLogTypeEnum logTypeEnum) {
        // 创建 Token 令牌
        OAuth2AccessTokenRespDTO accessTokenRespDTO = oauth2TokenApi
                .createAccessToken(new OAuth2AccessTokenCreateReqDTO()
                        .setUserId(user.getId()).setUserType(getUserType().getValue())
                        .setClientId(OAuth2ClientConstants.CLIENT_ID_DEFAULT))
                .getCheckedData();
        // 插入登陆日志
        createLoginLog(user.getId(), logTypeEnum, AppLoginResultEnum.SUCCESS);

        // 构建返回结果
        return AuthConvert.INSTANCE.convert(accessTokenRespDTO);
    }

    public void createLoginLog(Long userId, LoginLogTypeEnum logTypeEnum, AppLoginResultEnum loginResult) {
        // 插入客户端登录日志
        UserLoginLogSaveReqVO userLoginLogSaveReqVO = new UserLoginLogSaveReqVO();
        userLoginLogSaveReqVO.setUserId(userId);
        userLoginLogSaveReqVO.setLoginStatus(loginResult.getResult());
        userLoginLogSaveReqVO.setLoginTime(LocalDateTime.now());
        userLoginLogSaveReqVO.setLoginIp(getClientIP());
        userLoginLogSaveReqVO.setFailReason(loginResult.getMessage());
        userLoginLogSaveReqVO.setLoginType(logTypeEnum.getType());
        userLoginLogSaveReqVO.setLoginCity(IpToCityByLocalDB.getCityByIpLocal(getClientIP()));
        userLoginLogService.createLoginLogAsync(userLoginLogSaveReqVO);
    }

    @Override
    public void logout(String token) {
        // 删除访问令牌
        OAuth2AccessTokenRespDTO accessTokenRespDTO = oauth2TokenApi.removeAccessToken(token).getCheckedData();
        if (accessTokenRespDTO == null) {
            return;
        }
        // 删除成功，则记录登出日志
        createLogoutLog(accessTokenRespDTO.getUserId());
    }

    @Override
    public AppAuthLoginRespVO refreshToken(String refreshToken) {
        OAuth2AccessTokenRespDTO accessTokenDO = oauth2TokenApi.refreshAccessToken(refreshToken,
                OAuth2ClientConstants.CLIENT_ID_DEFAULT).getCheckedData();
        AppAuthLoginRespVO appAuthLoginRespVO = AuthConvert.INSTANCE.convert(accessTokenDO);

        return appAuthLoginRespVO;
    }

    @Override
    @Transactional
    public AppAuthLoginRespVO smsLogin(AppAuthSmsLoginReqVO reqVO) {
        // 校验验证码
        String userIp = getClientIP();
        smsCodeApi.useSmsCode(AuthConvert.INSTANCE.convert(reqVO, SmsSceneEnum.MEMBER_LOGIN.getScene(), userIp))
                .checkError();

        // 获得获得注册用户
        UserInfoDO user = userService.getUserInfoByMobile(reqVO.getMobile());

        // 校验是否禁用
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            createLoginLog(user.getId(), LoginLogTypeEnum.LOGIN_SMS, AppLoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user, LoginLogTypeEnum.LOGIN_SMS);
    }

    @Override
    public void sendSmsCode(Long userId, AppAuthSmsSendReqVO reqVO) {
        // 情况 3：如果是修改密码场景，需要查询手机号，无需前端传递
        /*
         * if (Objects.equals(reqVO.getScene(),
         * SmsSceneEnum.MEMBER_UPDATE_PASSWORD.getScene())) {
         * UserInfoDO user = userService.getInfo(userId);
         * reqVO.setMobile(user.getMobile());
         * } else {
         * // 判断是否传了手机号
         * if (StrUtil.isBlank(reqVO.getMobile())) {
         * throw exception(AUTH_MOBILE_NOT_BLANK);
         * }
         * }
         */
        // 情况 1：如果是修改手机场景，需要校验新手机号是否已经注册，说明不能使用该手机了
        if (Objects.equals(reqVO.getScene(), SmsSceneEnum.MEMBER_UPDATE_MOBILE.getScene())) {
            UserInfoDO user = userService.getUserInfoByMobile(reqVO.getMobile());
            if (user != null && !Objects.equals(user.getId(), userId)) {
                throw exception(AUTH_MOBILE_USED);
            }
        }
        // 情况 2：如果是重置密码场景，需要校验手机号是存在的
        if (Objects.equals(reqVO.getScene(), SmsSceneEnum.MEMBER_RESET_PASSWORD.getScene())
                || Objects.equals(reqVO.getScene(), SmsSceneEnum.MEMBER_LOGIN.getScene())) {
            UserInfoDO user = userService.getUserInfoByMobile(reqVO.getMobile());
            if (user == null) {
                throw exception(INFO_NOT_EXISTS);
            }
        }

        // 情况 4：如果是注册场景，需要校验手机号是否已经注册
        if (Objects.equals(reqVO.getScene(), SmsSceneEnum.MEMBER_REGISTER.getScene())) {
            UserInfoDO user = userService.getUserInfoByMobile(reqVO.getMobile());
            if (user != null) {
                throw exception(AUTH_MOBILE_USED);
            }
        }

        // 执行发送
        smsCodeApi.sendSmsCode(AuthConvert.INSTANCE.convert(reqVO).setCreateIp(getClientIP())).checkError();
    }

    private void createLogoutLog(Long userId) {
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(LoginLogTypeEnum.LOGOUT_SELF.getType());
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUserType(getUserType().getValue());
        reqDTO.setUsername(getMemberNo(userId));
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(getClientIP());
        reqDTO.setResult(LoginResultEnum.SUCCESS.getResult());
        loginLogApi.createLoginLog(reqDTO);
    }

    private String getMemberNo(Long userId) {
        if (userId == null) {
            return null;
        }
        UserInfoDO user = userService.getInfo(userId);
        return user != null ? user.getMobile() : null;
    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.MEMBER;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppAuthLoginRespVO register(AppAuthRegisterReqVO reqVO) {
        // 校验验证码
        String userIp = getClientIP();
        smsCodeApi.useSmsCode(AuthConvert.INSTANCE.convert(reqVO, SmsSceneEnum.MEMBER_REGISTER.getScene(), userIp))
                .checkError();

        // 校验手机号是否已注册
        UserInfoDO existUser = userService.getUserInfoByMobile(reqVO.getMobile());
        if (existUser != null) {
            throw exception(AUTH_MOBILE_USED);
        }

        // 创建用户
        UserInfoSaveReqVO createReqVO = new UserInfoSaveReqVO();
        createReqVO.setMobile(reqVO.getMobile());
        createReqVO.setPassword(passwordEncoder.encode(reqVO.getPassword())); // 加密密码
        createReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认启用
        createReqVO.setAuditStatus(AuditStatusEnum.PENDING_SUBMISSION.getStatus());
        Long userId = userService.createInfo(createReqVO);

        // 获取创建的用户
        UserInfoDO user = userService.getInfo(userId);

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user, LoginLogTypeEnum.LOGIN_SMS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(AppAuthResetPasswordReqVO reqVO) {
        // 校验验证码
        String userIp = getClientIP();
        smsCodeApi
                .useSmsCode(AuthConvert.INSTANCE.convert(reqVO, SmsSceneEnum.MEMBER_RESET_PASSWORD.getScene(), userIp))
                .checkError();

        // 校验用户是否存在
        UserInfoDO user = userService.getUserInfoByMobile(reqVO.getMobile());
        if (user == null) {
            throw exception(INFO_NOT_EXISTS);
        }

        // 更新密码
        UserInfoDO updateReqVO = new UserInfoDO();
        updateReqVO.setId(user.getId());
        updateReqVO.setMobile(user.getMobile());
        updateReqVO.setPassword(passwordEncoder.encode(reqVO.getNewPassword())); // 加密新密码
        userService.updateInfo(updateReqVO);
    }

    // ========== 邮箱认证相关 ==========

    @Override
    public AppAuthLoginRespVO emailLogin(AppAuthEmailLoginReqVO reqVO) {
        // 使用邮箱 + 密码，进行登录
        UserInfoDO user = emailLogin0(reqVO.getEmail(), reqVO.getPassword());
        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user, LoginLogTypeEnum.LOGIN_EMAIL_PASSWORD);
    }

    private UserInfoDO emailLogin0(String email, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_PASSWORD;
        // 校验账号是否存在
        UserInfoDO user = userService.getUserInfoByEmail(email);
        if (user == null) {
            createLoginLog(null, logTypeEnum, AppLoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!userService.isPasswordMatch(password, user.getPassword())) {
            createLoginLog(user.getId(), logTypeEnum, AppLoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 校验是否禁用
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            createLoginLog(user.getId(), logTypeEnum, AppLoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    @Override
    @Transactional
    public AppAuthLoginRespVO emailCodeLogin(AppAuthEmailCodeLoginReqVO reqVO) {
        // 校验邮箱验证码
        MailCodeUseReqDTO useReqDTO = new MailCodeUseReqDTO();
        useReqDTO.setMail(reqVO.getEmail());
        useReqDTO.setCode(reqVO.getCode());
        useReqDTO.setScene(SmsSceneEnum.MEMBER_LOGIN.getScene());
        mailSendApi.verifyMailCode(useReqDTO).checkError();

        // 获取用户
        UserInfoDO user = userService.getUserInfoByEmail(reqVO.getEmail());
        if (user == null) {
            throw exception(AUTH_EMAIL_NOT_EXISTS);
        }

        // 校验是否禁用
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            createLoginLog(user.getId(), LoginLogTypeEnum.LOGIN_SMS, AppLoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user, LoginLogTypeEnum.LOGIN_EMAIL);
    }

    @Override
    public void sendEmailCode(Long userId, AppAuthEmailSendReqVO reqVO) {
        // 如果是重置密码或登录场景，校验邮箱是否已注册
        if (Objects.equals(reqVO.getScene(), SmsSceneEnum.MEMBER_RESET_PASSWORD.getScene())
                || Objects.equals(reqVO.getScene(), SmsSceneEnum.MEMBER_LOGIN.getScene())) {
            UserInfoDO user = userService.getUserInfoByEmail(reqVO.getEmail());
            if (user == null) {
                throw exception(AUTH_EMAIL_NOT_EXISTS);
            }
        }

        // 如果是注册场景，校验邮箱是否已被使用
        if (Objects.equals(reqVO.getScene(), SmsSceneEnum.MEMBER_REGISTER.getScene())) {
            UserInfoDO user = userService.getUserInfoByEmail(reqVO.getEmail());
            if (user != null) {
                throw exception(AUTH_EMAIL_USED);
            }
        }
        SmsSceneEnum smsSceneEnum = SmsSceneEnum.getCodeByScene(reqVO.getScene());
        // 执行发送邮箱验证码
        MailCodeSendReqDTO sendReqDTO = new MailCodeSendReqDTO();
        sendReqDTO.setMail(reqVO.getEmail());
        sendReqDTO.setScene(reqVO.getScene());
        sendReqDTO.setUserId(userId);
        sendReqDTO.setUserType(getUserType().getValue());
        sendReqDTO.setTemplateCode(smsSceneEnum.getTemplateCode());
        mailSendApi.sendMailCode(sendReqDTO).checkError();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppAuthLoginRespVO emailRegister(AppAuthEmailRegisterReqVO reqVO) {
        // 校验邮箱验证码
        MailCodeUseReqDTO useReqDTO = new MailCodeUseReqDTO();
        useReqDTO.setMail(reqVO.getEmail());
        useReqDTO.setCode(reqVO.getCode());
        useReqDTO.setScene(SmsSceneEnum.MEMBER_REGISTER.getScene());
        mailSendApi.verifyMailCode(useReqDTO).checkError();

        // 校验邮箱是否已注册
        UserInfoDO existUser = userService.getUserInfoByEmail(reqVO.getEmail());
        if (existUser != null) {
            throw exception(AUTH_EMAIL_USED);
        }

        // 创建用户
        UserInfoSaveReqVO createReqVO = new UserInfoSaveReqVO();
        createReqVO.setEmail(reqVO.getEmail());
        createReqVO.setPassword(passwordEncoder.encode(reqVO.getPassword())); // 加密密码
        createReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认启用
        createReqVO.setAuditStatus(AuditStatusEnum.PENDING_SUBMISSION.getStatus());
        Long userId = userService.createInfo(createReqVO);

        // 获取创建的用户
        UserInfoDO user = userService.getInfo(userId);

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user, LoginLogTypeEnum.LOGIN_SMS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void emailResetPassword(AppAuthEmailResetPasswordReqVO reqVO) {
        // 校验邮箱验证码
        MailCodeUseReqDTO useReqDTO = new MailCodeUseReqDTO();
        useReqDTO.setMail(reqVO.getEmail());
        useReqDTO.setCode(reqVO.getCode());
        useReqDTO.setScene(SmsSceneEnum.MEMBER_RESET_PASSWORD.getScene());
        mailSendApi.verifyMailCode(useReqDTO).checkError();

        // 校验用户是否存在
        UserInfoDO user = userService.getUserInfoByEmail(reqVO.getEmail());
        if (user == null) {
            throw exception(AUTH_EMAIL_NOT_EXISTS);
        }

        // 更新密码
        UserInfoDO updateReqVO = new UserInfoDO();
        updateReqVO.setId(user.getId());
        updateReqVO.setPassword(passwordEncoder.encode(reqVO.getNewPassword())); // 加密新密码
        userService.updateInfo(updateReqVO);
    }

    // ========== 绑定手机号/邮箱 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindMobile(Long userId, AppAuthBindMobileReqVO reqVO) {
        // 1. 校验用户是否存在
        UserInfoDO user = userService.getInfo(userId);
        if (user == null) {
            throw exception(INFO_NOT_EXISTS);
        }

        // 2. 校验手机号是否已被使用
        UserInfoDO existUser = userService.getUserInfoByMobile(reqVO.getMobile());
        if (existUser != null && !existUser.getId().equals(userId)) {
            throw exception(AUTH_MOBILE_USED);
        }

        // 3. 校验手机验证码（发送到新手机号的）
        smsCodeApi.useSmsCode(new SmsCodeUseReqDTO()
                .setMobile(reqVO.getMobile())
                .setCode(reqVO.getSmsCode())
                .setScene(SmsSceneEnum.MEMBER_UPDATE_MOBILE.getScene())
                .setUsedIp(getClientIP()))
                .checkError();

        // 4. 校验邮箱验证码（发送到用户已绑定邮箱的）
        MailCodeUseReqDTO mailUseReqDTO = new MailCodeUseReqDTO();
        mailUseReqDTO.setMail(user.getEmail());
        mailUseReqDTO.setCode(reqVO.getMailCode());
        mailUseReqDTO.setScene(SmsSceneEnum.MEMBER_UPDATE_MOBILE.getScene());
        mailSendApi.verifyMailCode(mailUseReqDTO).checkError();

        // 5. 更新手机号
        UserInfoDO updateObj = new UserInfoDO();
        updateObj.setId(userId);
        updateObj.setMobile(reqVO.getMobile());
        userService.updateInfo(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindEmail(Long userId, AppAuthBindEmailReqVO reqVO) {
        // 1. 校验用户是否存在
        UserInfoDO user = userService.getInfo(userId);
        if (user == null) {
            throw exception(INFO_NOT_EXISTS);
        }

        // 2. 校验邮箱是否已被使用
        UserInfoDO existUser = userService.getUserInfoByEmail(reqVO.getEmail());
        if (existUser != null && !existUser.getId().equals(userId)) {
            throw exception(AUTH_EMAIL_USED);
        }

        // 3. 校验手机验证码（发送到用户已绑定手机的）
        smsCodeApi.useSmsCode(new SmsCodeUseReqDTO()
                .setMobile(user.getMobile())
                .setCode(reqVO.getSmsCode())
                .setScene(SmsSceneEnum.MEMBER_UPDATE_MOBILE.getScene())
                .setUsedIp(getClientIP()))
                .checkError();

        // 4. 校验邮箱验证码（发送到新邮箱的）
        MailCodeUseReqDTO mailUseReqDTO = new MailCodeUseReqDTO();
        mailUseReqDTO.setMail(reqVO.getEmail());
        mailUseReqDTO.setCode(reqVO.getMailCode());
        mailUseReqDTO.setScene(SmsSceneEnum.MEMBER_UPDATE_MOBILE.getScene());
        mailSendApi.verifyMailCode(mailUseReqDTO).checkError();

        // 5. 更新邮箱
        UserInfoDO updateObj = new UserInfoDO();
        updateObj.setId(userId);
        updateObj.setEmail(reqVO.getEmail());
        userService.updateInfo(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppAuthBind2FARespVO init2FABinding(Long userId) {
        // 校验用户是否存在
        UserInfoDO user = userService.getInfo(userId);
        if (user == null) {
            throw exception(INFO_NOT_EXISTS);
        }
        // 如果用户开启了2FA，就不能再调用初始化
        if (F2AEnum.ENABLED.getStatus().equals(user.getTwoFactorAuthStatus())) {
            throw exception(F2A_ALREADY_BOUND);
        }

        // 生成2FA密钥
        String secret = TwoFactorAuthUtils.generateSecretKey();

        // 生成二维码URL (使用手机号作为账号标识)
        String qrCodeUrl = TwoFactorAuthUtils.generateQRCodeUrl(secret, user.getMobile(), "RWA-Platform");

        // 临时保存密钥到用户表(状态为待验证)
        UserInfoDO updateReqVO = new UserInfoDO();
        updateReqVO.setId(userId);
        updateReqVO.setMobile(user.getMobile());
        updateReqVO.setPassword(user.getPassword());
        updateReqVO.setTwoFactorAuthSecret(secret);
        updateReqVO.setTwoFactorAuthStatus(F2AEnum.PENDING_VERIFICATION.getStatus()); // 2-待验证
        userService.updateInfo(updateReqVO);

        return AppAuthBind2FARespVO.builder()
                .secret(secret)
                .qrCodeUrl(qrCodeUrl)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bind2FA(Long userId, AppAuthBind2FAReqVO reqVO) {
        // 校验用户是否存在
        UserInfoDO user = userService.getInfo(userId);
        if (user == null) {
            throw exception(INFO_NOT_EXISTS);
        }

        // 校验是否已初始化2FA
        if (user.getTwoFactorAuthSecret() == null
                || !Objects.equals(user.getTwoFactorAuthStatus(), F2AEnum.PENDING_VERIFICATION.getStatus())) {
            throw exception(F2A_NOT_INIT);
        }

        // 验证2FA验证码
        if (!TwoFactorAuthUtils.verifyCode(user.getTwoFactorAuthSecret(), reqVO.getCode())) {
            throw exception(F2A_VERIFY_CODE_ERROR);
        }

        // 更新2FA状态为已开启
        UserInfoDO updateReqVO = new UserInfoDO();
        updateReqVO.setId(userId);
        updateReqVO.setMobile(user.getMobile());
        updateReqVO.setPassword(user.getPassword());
        updateReqVO.setTwoFactorAuthStatus(F2AEnum.ENABLED.getStatus()); // 1-已开启
        updateReqVO.setTwoFactorAuthBindTime(LocalDateTime.now());
        userService.updateInfo(updateReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean verify2FA(Long userId, String code) {
        // 校验用户是否存在
        UserInfoDO user = userService.getInfo(userId);
        if (user == null) {
            return false;
        }

        // 校验是否已开启2FA
        if (user.getTwoFactorAuthStatus() != 1 || user.getTwoFactorAuthSecret() == null) {
            return false;
        }

        // 验证2FA验证码
        boolean verified = TwoFactorAuthUtils.verifyCode(user.getTwoFactorAuthSecret(), code);

        if (verified) {
            // 更新最后验证时间
            UserInfoDO updateReqVO = new UserInfoDO();
            updateReqVO.setId(userId);
            updateReqVO.setTwoFactorAuthLastVerifyTime(LocalDateTime.now());
            userService.updateInfo(updateReqVO);
        }

        return verified;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultCurrency(Long userId, AppAuthSetCurrencyReqVO reqVO) {
        // 校验用户是否存在
        UserInfoDO user = userService.getInfo(userId);
        if (user == null) {
            throw exception(INFO_NOT_EXISTS);
        }

        // 更新默认币种
        UserInfoDO updateReqVO = new UserInfoDO();
        updateReqVO.setId(userId);
        updateReqVO.setDefaultCurrency(reqVO.getCurrency());
        userInfoMapper.updateById(updateReqVO);
    }

    @Override
    public AppUserInfoRespVO getUserInfo(Long userId) {
        // 校验用户是否存在
        UserInfoDO user = userService.getInfo(userId);
        if (user == null) {
            throw exception(INFO_NOT_EXISTS);
        }

        // 构建响应VO
        AppUserInfoRespVO respVO = new AppUserInfoRespVO();
        respVO.setMobile(user.getMobile());
        respVO.setRealName(user.getRealName());
        respVO.setAuditStatus(user.getAuditStatus());
        respVO.setDefaultCurrency(user.getDefaultCurrency());
        respVO.setTwoFactorAuthStatus(user.getTwoFactorAuthStatus());
        respVO.setId(userId);
        respVO.setNickName(user.getNickName());
        respVO.setAvatar(user.getAvatar());
        respVO.setEmail(user.getEmail());
        LambdaQueryWrapper<UserChainDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserChainDO::getUserId, userId);
        queryWrapper.ne(UserChainDO::getChainStatus, ChainAddressStatusEnum.UNBIND.getStatus());
        Long count = userChainMapper.selectCount(queryWrapper);
        respVO.setChainCount(count.intValue());

        // 查询用户订单数量
        int orderCount = userInfoMapper.getUserOrderCount(userId);
        respVO.setOrderCount(orderCount);

        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void requestUnbind2FA(AppAuthUnbind2FAReqVO reqVO) {
        Long userId = getLoginUserId();
        // 校验用户是否存在
        UserInfoDO user = userService.getInfo(userId);
        if (user == null) {
            throw exception(INFO_NOT_EXISTS);
        }

        if(StringUtil.isNotBlank(reqVO.getEmailCode()) && StringUtil.isNotBlank(user.getEmail())){
            // 校验邮箱验证码
            MailCodeUseReqDTO mailCodeUseReq = new MailCodeUseReqDTO();
            mailCodeUseReq.setMail(user.getEmail());
            mailCodeUseReq.setScene(SmsSceneEnum.MEMBER_2FA_UNBIND.getScene());
            mailCodeUseReq.setCode(reqVO.getEmailCode());

            mailSendApi.verifyMailCode(mailCodeUseReq).checkError();
        }else{
            SmsCodeUseReqDTO smsCodeUseReq = new SmsCodeUseReqDTO();
            smsCodeUseReq.setMobile(user.getMobile());
            smsCodeUseReq.setScene(SmsSceneEnum.MEMBER_2FA_UNBIND.getScene());
            smsCodeUseReq.setCode(reqVO.getCode());
            smsCodeUseReq.setUsedIp(ServletUtils.getClientIP());

            smsCodeApi.useSmsCode(smsCodeUseReq)
                    .checkError();
        }



        // 校验是否已开启2FA
        if (!Objects.equals(user.getTwoFactorAuthStatus(), F2AEnum.ENABLED.getStatus())) {
            throw exception(F2A_NOT_ENABLED);
        }

        // 更新2FA状态为解绑中
        UserInfoDO updateReqVO = new UserInfoDO();
        updateReqVO.setId(userId);
        updateReqVO.setTwoFactorAuthStatus(F2AEnum.UNBINDING.getStatus()); // 3-解绑中
        userInfoMapper.updateById(updateReqVO);
    }

    public AuthServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

    @Override
    @SneakyThrows
    public void updateProfile(Long userId, AppUserInfoUpdateReqVO reqVO) {
        // 校验用户是否存在
        UserInfoDO user = userService.getInfo(userId);
        if (user == null) {
            throw exception(INFO_NOT_EXISTS);
        }

        // 更新用户基础信息
        UserInfoDO updateReqVO = new UserInfoDO();
        updateReqVO.setId(userId);
        updateReqVO.setNickName(reqVO.getNickName());

        if (reqVO.getAvatarFile() != null && !reqVO.getAvatarFile().isEmpty()) {
            String avatar = fileApi.createFile(reqVO.getAvatarFile().getOriginalFilename(),
                    IoUtil.readBytes(reqVO.getAvatarFile().getInputStream()));
            updateReqVO.setAvatar(avatar);
        }

        userInfoMapper.updateById(updateReqVO);
    }
}
