package cc.bamboo.module.system.service.mail;

import cc.bamboo.module.system.mq.message.mail.MailSendMessage;

import java.util.Map;

/**
 * 邮件发送 Service 接口
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
public interface MailSendService {

    /**
     * 发送单条邮件给管理后台的用户
     *
     * @param mail           邮箱
     * @param userId         用户编码
     * @param templateCode   邮件模版编码
     * @param templateParams 邮件模版参数
     * @return 发送日志编号
     */
    Long sendSingleMailToAdmin(String mail, Long userId,
            String templateCode, Map<String, Object> templateParams);

    /**
     * 发送单条邮件给用户 APP 的用户
     *
     * @param mail           邮箱
     * @param userId         用户编码
     * @param templateCode   邮件模版编码
     * @param templateParams 邮件模版参数
     * @return 发送日志编号
     */
    Long sendSingleMailToMember(String mail, Long userId,
            String templateCode, Map<String, Object> templateParams);

    /**
     * 发送单条邮件给用户
     *
     * @param mail           邮箱
     * @param userId         用户编码
     * @param userType       用户类型
     * @param templateCode   邮件模版编码
     * @param templateParams 邮件模版参数
     * @return 发送日志编号
     */
    Long sendSingleMail(String mail, Long userId, Integer userType,
            String templateCode, Map<String, Object> templateParams);

    /**
     * 执行真正的邮件发送
     * 注意，该方法仅仅提供给 MQ Consumer 使用
     *
     * @param message 邮件
     */
    void doSendMail(MailSendMessage message);

    /**
     * 发送邮箱验证码（创建验证码 + 发送邮件一站式方法）
     * templateParams 会自动设为 {"code": 生成的验证码}
     *
     * @param mail         邮箱地址
     * @param userId       用户编号
     * @param userType     用户类型
     * @param templateCode 邮件模版编码
     * @param scene        验证码场景
     * @return 发送日志编号
     */
    Long sendMailCode(String mail, Long userId, Integer userType,
            String templateCode, Integer scene);

    /**
     * 创建邮箱验证码（存储在 Redis 中）
     *
     * @param mail  邮箱地址
     * @param scene 发送场景
     * @return 验证码
     */
    String createMailCode(String mail, Integer scene);

    /**
     * 使用（验证）邮箱验证码
     *
     * @param mail  邮箱地址
     * @param code  验证码
     * @param scene 发送场景
     */
    boolean useMailCode(String mail, String code, Integer scene);

}
