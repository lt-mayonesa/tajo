package io.lmayo.tajo.domain

import io.lmayo.tajo.support.CompanionLogger
import io.lmayo.tajo.support.DateTimeProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Service
import java.net.URI
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Service
class ShortURLService(
    private val shortCodeProvider: ShortCodeProvider,
    private val dateTimeProvider: DateTimeProvider,
    private val opsForValue: ReactiveValueOperations<String, String>
) {

    suspend fun generate(url: URI, shortCode: Code? = null): URI? {
        var code = (shortCode ?: shortCodeProvider.random())
            .log { "generate: code to use {}" to listOf(it) }

        var valueSet = url.storeWith(code)
            .log { "generate: set if absent returned {}" to listOf(it) }

        if (shortCode != null && !valueSet)
            return null

        while (!valueSet) {
            code = shortCodeProvider.random()
            valueSet = url.storeWith(code)
            log { "generate: retried storage of code {}, result {}" to listOf(code, valueSet) }
        }

        opsForValue.setAndAwait(code.createdAtKey, dateTimeNow())

        return URI.create("http://localhost:8080/$code")
    }

    suspend fun find(code: Code): URI? =
        opsForValue.getAndAwait(code.token)
            .log { "find: redis GET returned {}" to listOf(it) }
            ?.let { URI.create(it) }
            ?.also {
                opsForValue.incrementAndAwait(code.counterKey)
                opsForValue.setAndAwait(code.accessedAtKey, dateTimeNow())
            }

    suspend fun stats(code: Code): Stats? = coroutineScope {
        opsForValue.getAndAwait(code.token)
            ?.let { URI.create(it) }
            ?.let {
                val asyncAccessedAt = async { opsForValue.getAndAwait(code.accessedAtKey) }
                val asyncCreatedAt = async { opsForValue.getAndAwait(code.createdAtKey) }
                val asyncCounter = async { opsForValue.getAndAwait(code.counterKey) }
                Stats(
                    code, it,
                    createdAt = OffsetDateTime.parse(asyncCreatedAt.await()!!),
                    accessedAt = asyncAccessedAt.await()?.let { d -> OffsetDateTime.parse(d) },
                    accessedCount = asyncCounter.await()?.toInt() ?: 0
                )
            }
    }

    private suspend inline fun URI.storeWith(code: Code) =
        opsForValue.setIfAbsentAndAwait(code.token, this.toString())

    private fun dateTimeNow(): String =
        dateTimeProvider.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    private val Code.counterKey: String
        get() = "$token:counter"

    private val Code.createdAtKey: String
        get() = "$token:created_at"

    private val Code.accessedAtKey: String
        get() = "$token:accessed_at"

    companion object : CompanionLogger()

}