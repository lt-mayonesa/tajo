package io.lmayo.tajo.api

import io.lmayo.tajo.domain.Code
import io.lmayo.tajo.domain.ShortURLService
import io.lmayo.tajo.domain.Stats
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RestController
class URLStatsController(
    private val shortURLService: ShortURLService
) {

    @GetMapping("/stats/{code}")
    suspend fun statsURL(@PathVariable code: String): ResponseEntity<StatsResponse> =
        shortURLService.stats(Code(code))
            ?.let { ResponseEntity.ok(it.toResponse()) }
            ?: ResponseEntity.notFound().build()

    private fun Stats.toResponse(): StatsResponse =
        StatsResponse(
            code = this.code.token,
            url = this.url.toString(),
            createdAt = this.createdAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            accessedAt = this.accessedAt?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            accessedCount = this.accessedCount
        )
}

data class StatsResponse(
    val code: String,
    val url: String,
    val createdAt: String,
    val accessedAt: String?,
    val accessedCount: Int
)
