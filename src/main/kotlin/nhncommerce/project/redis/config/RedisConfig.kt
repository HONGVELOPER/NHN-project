package nhncommerce.project.redis.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Value("\${spring.redis.host}")
    private val host = ""

    @Value("\${spring.redis.port}")
    private val port = 0

    @Bean
    fun redisConnectionFactory() : RedisConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }

    @Bean
    fun redisTemplate() : RedisTemplate<*, *> {
        val redisTemplate: RedisTemplate<*, *> = RedisTemplate<Any, Any>()
        redisTemplate.keySerializer = StringRedisSerializer() //저장시 이름관련
        redisTemplate.valueSerializer = GenericJackson2JsonRedisSerializer()

        redisTemplate.setConnectionFactory(redisConnectionFactory())
        return redisTemplate
    }
}