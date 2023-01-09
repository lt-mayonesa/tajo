package io.lmayo.tajo.domain

import java.net.URI
import java.time.OffsetDateTime

data class Stats(
    val code: Code,
    val url: URI,
    val createdAt: OffsetDateTime,
    val accessedAt: OffsetDateTime?,
    val accessedCount: Int
)
