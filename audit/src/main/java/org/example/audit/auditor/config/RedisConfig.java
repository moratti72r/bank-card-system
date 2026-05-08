package org.example.audit.auditor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Ключи — строки
        template.setKeySerializer(new StringRedisSerializer());
        // Значения — JSON-объекты (поддержка десериализации)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // То же для хэшей, если используются
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Важно: инициализировать после установки сериализаторов
        template.afterPropertiesSet();

        return template;
    }
}
