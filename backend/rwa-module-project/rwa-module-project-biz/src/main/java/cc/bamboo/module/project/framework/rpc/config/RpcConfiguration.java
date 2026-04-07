package cc.bamboo.module.project.framework.rpc.config;

import cc.bamboo.module.chain.api.dividend.DividendRecordApi;
import cc.bamboo.module.infra.api.config.ConfigApi;
import cc.bamboo.module.infra.api.file.FileApi;
import cc.bamboo.module.infra.api.websocket.WebSocketSenderApi;
import cc.bamboo.module.system.api.mail.MailSendApi;
import cc.bamboo.module.system.api.sms.SmsCodeApi;
import cc.bamboo.module.user.api.noticemessage.NoticeMessageApi;
import cc.bamboo.module.user.api.userinfo.UserInfoApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {MailSendApi.class,DividendRecordApi.class, FileApi.class, WebSocketSenderApi.class, ConfigApi.class, UserInfoApi.class, SmsCodeApi.class, NoticeMessageApi.class})
public class RpcConfiguration {
}
