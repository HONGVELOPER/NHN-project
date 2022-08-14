package nhncommerce.project.redis.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    @Bean
    fun redisConnectionFactory() : RedisConnectionFactory {
        return LettuceConnectionFactory("localhost", 6379)
    }

    @Bean
    fun redisTemplate() : RedisTemplate<*, *> {
        val redisTemplate: RedisTemplate<*, *> = RedisTemplate<Any, Any>()
        redisTemplate.setKeySerializer(StringRedisSerializer()) //저장시 이름관련
        redisTemplate.setValueSerializer(GenericJackson2JsonRedisSerializer())

        redisTemplate.setConnectionFactory(redisConnectionFactory())
        return redisTemplate
    }
}