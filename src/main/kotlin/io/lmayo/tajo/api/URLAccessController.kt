package io.lmayo.tajo.api

import io.lmayo.tajo.domain.ShortURLGeneratorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class URLAccessController(
    private val shortURLGenerator: ShortURLGeneratorService
) {

    @GetMapping("/{code}")
    suspend fun accessURL(@PathVariable code: String): ResponseEntity<Unit> =
        shortURLGenerator.find(code)
            ?.let { ResponseEntity.status(HttpStatus.FOUND).location(it).build() }
            ?: ResponseEntity.notFound().build()

}