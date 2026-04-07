package cc.bamboo.module.user.framework.rpc.config;

import cc.bamboo.module.infra.api.config.ConfigApi;
import cc.bamboo.module.infra.api.file.FileApi;
import cc.bamboo.module.infra.api.websocket.WebSocketSenderApi;
import cc.bamboo.module.project.api.purchase.UserPurchaseSummaryApi;
import cc.bamboo.module.system.api.logger.LoginLogApi;
import cc.bamboo.module.system.api.mail.MailSendApi;
import cc.bamboo.module.system.api.sms.SmsCodeApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "userRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {SmsCodeApi.class, LoginLogApi.class, FileApi.class, MailSendApi.class, UserPurchaseSummaryApi.class})

public class RpcConfiguration {
}
