package cc.bamboo.module.system.service.mail;

import cc.bamboo.framework.common.exception.ErrorCode;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cc.bamboo.framework.common.enums.CommonStatusEnum;
import cc.bamboo.framework.common.enums.UserTypeEnum;
import cc.bamboo.framework.redis.service.RedisService;
import cc.bamboo.module.system.dal.dataobject.mail.MailAccountDO;
import cc.bamboo.module.system.dal.dataobject.mail.MailTemplateDO;
import cc.bamboo.module.system.dal.dataobject.user.AdminUserDO;
import cc.bamboo.module.system.mq.message.mail.MailSendMessage;
import cc.bamboo.module.system.mq.producer.mail.MailProducer;
import cc.bamboo.module.system.service.member.MemberService;
import cc.bamboo.module.system.service.user.AdminUserService;
import cc.bamboo.module.system.framework.sms.config.SmsCodeProperties;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.util.RandomUtil.randomInt;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.system.enums.ErrorCodeConstants.*;

/**
 * 邮箱发送 Service 实现类
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Service
@Validated
@Slf4j
public class MailSendServiceImpl implements MailSendService {

    @Resource
    private AdminUserService adminUserService;
    @Resource
    private MemberService memberService;

    @Resource
    private MailAccountService mailAccountService;
    @Resource
    private MailTemplateService mailTemplateService;

    @Resource
    private MailLogService mailLogService;
    @Resource
    private MailProducer mailProducer;

    @Resource
    private RedisService redisService;

    @Resource
    private SmsCodeProperties smsCodeProperties;

    /** Redis Key：邮箱验证码，格式 mail:code:{scene}:{mail} */
    private static final String MAIL_CODE_KEY = "mail:code:%d:%s";
    /** Redis Key：邮箱验证码发送频率限制，格式 mail:code:freq:{scene}:{mail} */
    private static final String MAIL_CODE_FREQ_KEY = "mail:code:freq:%d:%s";
    /** Redis Key：邮箱验证码每日发送次数，格式 mail:code:daily:{scene}:{mail} */
    private static final String MAIL_CODE_DAILY_KEY = "mail:code:daily:%d:%s";

    @Override
    public Long sendSingleMailToAdmin(String mail, Long userId,
            String templateCode, Map<String, Object> templateParams) {
        // 如果 mail 为空，则加载用户编号对应的邮箱
        if (StrUtil.isEmpty(mail)) {
            AdminUserDO user = adminUserService.getUser(userId);
            if (user != null) {
                mail = user.getEmail();
            }
        }
        // 执行发送
        return sendSingleMail(mail, userId, UserTypeEnum.ADMIN.getValue(), templateCode, templateParams);
    }

    @Override
    public Long sendSingleMailToMember(String mail, Long userId,
            String templateCode, Map<String, Object> templateParams) {
        // 如果 mail 为空，则加载用户编号对应的邮箱
        if (StrUtil.isEmpty(mail)) {
            mail = memberService.getMemberUserEmail(userId);
        }
        // 执行发送
        return sendSingleMail(mail, userId, UserTypeEnum.MEMBER.getValue(), templateCode, templateParams);
    }

    @Override
    public Long sendSingleMail(String mail, Long userId, Integer userType,
            String templateCode, Map<String, Object> templateParams) {
        // 校验邮箱模版是否合法
        MailTemplateDO template = validateMailTemplate(templateCode);
        // 校验邮箱账号是否合法
        MailAccountDO account = validateMailAccount(template.getAccountId());

        // 校验邮箱是否存在
        mail = validateMail(mail);
        validateTemplateParams(template, templateParams);

        // 创建发送日志。如果模板被禁用，则不发送短信，只记录日志
        Boolean isSend = CommonStatusEnum.ENABLE.getStatus().equals(template.getStatus());
        String title = mailTemplateService.formatMailTemplateContent(template.getTitle(), templateParams);
        String content = mailTemplateService.formatMailTemplateContent(template.getContent(), templateParams);
        Long sendLogId = mailLogService.createMailLog(userId, userType, mail,
                account, template, content, templateParams, isSend);
        // 发送 MQ 消息，异步执行发送短信
        if (isSend) {
            mailProducer.sendMailSendMessage(sendLogId, mail, account.getId(),
                    template.getNickname(), title, content);
        }
        return sendLogId;
    }

    @Override
    public void doSendMail(MailSendMessage message) {
        // 1. 创建发送账号
        MailAccountDO account = validateMailAccount(message.getAccountId());
        MailAccount mailAccount = buildMailAccount(account, message.getNickname());
        // 2. 发送邮件
        try {
            String messageId = MailUtil.send(mailAccount, message.getMail(),
                    message.getTitle(), message.getContent(), true);
            // 3. 更新结果（成功）
            mailLogService.updateMailSendResult(message.getLogId(), messageId, null);
        } catch (Exception e) {
            // 3. 更新结果（异常）
            mailLogService.updateMailSendResult(message.getLogId(), null, e);
        }
    }

    // ========== 邮箱验证码相关 ==========

    @Override
    public Long sendMailCode(String mail, Long userId, Integer userType,
            String templateCode, Integer scene) {
        // 1. 创建验证码（含频率、每日上限校验）
        String code = createMailCode(mail, scene);
        // 2. 发送邮件，templateParams 自动设为 {"code": 验证码}
        Map<String, Object> templateParams = cn.hutool.core.map.MapUtil.of("code", code);

        if (Boolean.TRUE.equals(smsCodeProperties.getEnableSend())) {
          return   sendSingleMail(mail, userId, userType, templateCode, templateParams);
        } else {
                log.info("[sendSmsCode][跳过邮件发送] mobile: {}, code: {}, scene: {}",
                    mail, code, scene);
                return null;
        }

    }

    @Override
    public String createMailCode(String mail, Integer scene) {
        // 1. 校验发送频率（从 smsCodeProperties 获取频率配置）
        String freqKey = String.format(MAIL_CODE_FREQ_KEY, scene, mail);
        if (Boolean.TRUE.equals(redisService.hasKey(freqKey))) {
            throw exception(MAIL_CODE_SEND_TOO_FAST);
        }

        // 2. 校验每日发送上限
        String dailyKey = String.format(MAIL_CODE_DAILY_KEY, scene, mail);
        Integer todayCount = redisService.getCacheObject(dailyKey);
        if (todayCount != null && todayCount >= smsCodeProperties.getSendMaximumQuantityPerDay()) {
            throw exception(SMS_CODE_EXCEED_SEND_MAXIMUM_QUANTITY_PER_DAY);
        }

        // 3. 生成验证码（使用 smsCodeProperties 的 beginCode ~ endCode 范围）
        String code = String.format("%0" + smsCodeProperties.getEndCode().toString().length() + "d",
                randomInt(smsCodeProperties.getBeginCode(), smsCodeProperties.getEndCode() + 1));

        // 4. 存储验证码到 Redis，过期时间取 smsCodeProperties.expireTimes
        String codeKey = String.format(MAIL_CODE_KEY, scene, mail);
        long expireSeconds = smsCodeProperties.getExpireTimes().getSeconds();
        redisService.setCacheObject(codeKey, code, expireSeconds, TimeUnit.SECONDS);

        // 5. 设置发送频率限制（取 smsCodeProperties.sendFrequency）
        long freqSeconds = smsCodeProperties.getSendFrequency().getSeconds();
        redisService.setCacheObject(freqKey, "1", freqSeconds, TimeUnit.SECONDS);

        // 6. 更新每日发送计数（当天结束自动过期）
        if (todayCount == null) {
            // 首次发送，初始化计数并设置到当天结束的过期时间
            redisService.setCacheObject(dailyKey, 1, getSecondsUntilEndOfDay(), TimeUnit.SECONDS);
        } else {
            redisService.setCacheObject(dailyKey, todayCount + 1);
            // 保留原有过期时间，不重新设置
        }

        log.info("[createMailCode] 邮箱验证码已创建，邮箱: {}, 场景: {}, 验证码: {}, 今日第{}次",
                mail, scene, code, (todayCount == null ? 1 : todayCount + 1));
        return code;
    }

    @Override
    public boolean useMailCode(String mail, String code, Integer scene) {
        // 1. 从 Redis 获取验证码
        String codeKey = String.format(MAIL_CODE_KEY, scene, mail);
        String cachedCode = redisService.getCacheObject(codeKey);

        // 2. 验证码不存在或已过期
        if (cachedCode == null) {
            throw exception(MAIL_CODE_INVALID);
        }

        // 3. 验证码不匹配
        if (!cachedCode.equals(code)) {
            throw exception(MAIL_CODE_INVALID);
        }

        // 4. 验证成功，删除验证码（一次性使用）
        redisService.deleteObject(codeKey);
        log.info("[useMailCode] 邮箱验证码验证成功，邮箱: {}, 场景: {}", mail, scene);
        return true;
    }

    /**
     * 计算当前时间到当天结束（23:59:59）的剩余秒数
     */
    private long getSecondsUntilEndOfDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        return Duration.between(now, endOfDay).getSeconds() + 60L;
    }

    // ========== 内部方法 ==========

    private MailAccount buildMailAccount(MailAccountDO account, String nickname) {
        String from = StrUtil.isNotEmpty(nickname) ? nickname + " <" + account.getMail() + ">" : account.getMail();
        return new MailAccount().setFrom(from).setAuth(true)
                .setUser(account.getUsername()).setPass(account.getPassword())
                .setHost(account.getHost()).setPort(account.getPort())
                .setSslEnable(account.getSslEnable()).setStarttlsEnable(account.getStarttlsEnable());
    }

    @VisibleForTesting
    MailTemplateDO validateMailTemplate(String templateCode) {
        // 获得邮件模板。考虑到效率，从缓存中获取
        MailTemplateDO template = mailTemplateService.getMailTemplateByCodeFromCache(templateCode);
        // 邮件模板不存在
        if (template == null) {
            throw exception(MAIL_TEMPLATE_NOT_EXISTS);
        }
        return template;
    }

    @VisibleForTesting
    MailAccountDO validateMailAccount(Long accountId) {
        // 获得邮箱账号。考虑到效率，从缓存中获取
        MailAccountDO account = mailAccountService.getMailAccountFromCache(accountId);
        // 邮箱账号不存在
        if (account == null) {
            throw exception(MAIL_ACCOUNT_NOT_EXISTS);
        }
        return account;
    }

    @VisibleForTesting
    String validateMail(String mail) {
        if (StrUtil.isEmpty(mail)) {
            throw exception(MAIL_SEND_MAIL_NOT_EXISTS);
        }
        return mail;
    }

    /**
     * 校验邮件参数是否确实
     *
     * @param template       邮箱模板
     * @param templateParams 参数列表
     */
    @VisibleForTesting
    void validateTemplateParams(MailTemplateDO template, Map<String, Object> templateParams) {
        template.getParams().forEach(key -> {
            Object value = templateParams.get(key);
            if (value == null) {
                throw exception(MAIL_SEND_TEMPLATE_PARAM_MISS, key);
            }
        });
    }

}
