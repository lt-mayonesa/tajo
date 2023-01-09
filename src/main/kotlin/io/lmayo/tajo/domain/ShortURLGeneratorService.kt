package io.lmayo.tajo.domain

import io.lettuce.core.RedisClient
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import java.net.URI

@Service
class ShortURLGeneratorService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>
) {

    fun generate(url: URI, shortCode: String? = null): URI {
        TODO()
    }

}