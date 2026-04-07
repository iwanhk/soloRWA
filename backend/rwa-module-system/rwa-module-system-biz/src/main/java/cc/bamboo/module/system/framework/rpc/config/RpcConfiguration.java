package cc.bamboo.module.system.framework.rpc.config;

import cc.bamboo.module.infra.api.config.ConfigApi;
import cc.bamboo.module.infra.api.file.FileApi;
import cc.bamboo.module.infra.api.websocket.WebSocketSenderApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, WebSocketSenderApi.class, ConfigApi.class})
public class RpcConfiguration {
}
