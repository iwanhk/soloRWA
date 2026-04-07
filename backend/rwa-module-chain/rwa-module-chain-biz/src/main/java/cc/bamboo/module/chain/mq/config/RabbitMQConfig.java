package cc.bamboo.module.chain.mq.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ通用配置
 * 支持多种消息类型的配置
 *
 * @author Swolf
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 配置消息转换器
     * 使用Jackson2JsonMessageConverter将消息转换为JSON格式
     *
     * @return MessageConverter
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
