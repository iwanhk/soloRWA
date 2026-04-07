package cc.bamboo.framework.idempotent.config;

import cc.bamboo.framework.idempotent.core.aop.IdempotentAspect;
import cc.bamboo.framework.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import cc.bamboo.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import cc.bamboo.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import cc.bamboo.framework.idempotent.core.keyresolver.impl.UserIdempotentKeyResolver;
import cc.bamboo.framework.idempotent.core.redis.IdempotentRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import cc.bamboo.framework.redis.config.RwaRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@AutoConfiguration(after = RwaRedisAutoConfiguration.class)
public class RwaIdempotentConfiguration {

    @Bean
    public IdempotentAspect idempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        return new IdempotentAspect(keyResolvers, idempotentRedisDAO);
    }

    @Bean
    public IdempotentRedisDAO idempotentRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentRedisDAO(stringRedisTemplate);
    }

    // ========== 各种 IdempotentKeyResolver Bean ==========

    @Bean
    public DefaultIdempotentKeyResolver defaultIdempotentKeyResolver() {
        return new DefaultIdempotentKeyResolver();
    }

    @Bean
    public UserIdempotentKeyResolver userIdempotentKeyResolver() {
        return new UserIdempotentKeyResolver();
    }

    @Bean
    public ExpressionIdempotentKeyResolver expressionIdempotentKeyResolver() {
        return new ExpressionIdempotentKeyResolver();
    }

}
