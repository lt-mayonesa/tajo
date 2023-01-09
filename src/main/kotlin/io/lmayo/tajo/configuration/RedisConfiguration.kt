package io.lmayo.tajo.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations

@Configuration
class RedisConfiguration {

    @Bean
    fun opsForValue(redisTemplate: ReactiveRedisTemplate<String, String>): ReactiveValueOperations<String, String> =
        redisTemplate.opsForValue()

}