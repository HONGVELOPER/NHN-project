package nhncommerce.project.redis.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession

@EnableRedisHttpSession
@Configuration
class RedisConfig {

    @Value("\${spring.redis.host}")
    private val host = ""

    @Value("\${spring.redis.port}")
    private val port = 0

    @Value("\${spring.redis.password}")
    private val password = ""

    @Bean
    fun redisConnectionFactory() : RedisConnectionFactory {
        val lettuceConnectionFactory = LettuceConnectionFactory(host, port)
        lettuceConnectionFactory.setPassword(password)

        return lettuceConnectionFactory
    }

    @Bean
    fun redisTemplate() : RedisTemplate<*, *> {
        val redisTemplate: RedisTemplate<*, *> = RedisTemplate<Any, Any>()

        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = GenericJackson2JsonRedisSerializer()

        redisTemplate.hashKeySerializer = GenericJackson2JsonRedisSerializer()
        redisTemplate.hashValueSerializer = GenericJackson2JsonRedisSerializer() //

        redisTemplate.setConnectionFactory(redisConnectionFactory())
        return redisTemplate
    }
}