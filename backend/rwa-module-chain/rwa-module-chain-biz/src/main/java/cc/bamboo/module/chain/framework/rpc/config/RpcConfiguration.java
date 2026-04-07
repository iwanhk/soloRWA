package cc.bamboo.module.chain.framework.rpc.config;

import cc.bamboo.module.infra.api.config.ConfigApi;
import cc.bamboo.module.infra.api.file.FileApi;
import cc.bamboo.module.infra.api.websocket.WebSocketSenderApi;
import cc.bamboo.module.project.api.projectinfo.ProjectInfoApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, WebSocketSenderApi.class, ConfigApi.class, ProjectInfoApi.class})
public class RpcConfiguration {
}
