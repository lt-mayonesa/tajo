package io.lmayo.tajo.api

import io.lmayo.tajo.domain.ShortURLGeneratorService
import jakarta.validation.GroupSequence
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI


@RestController
class URLSubmitController(
    private val shortURLGenerator: ShortURLGeneratorService
) {

    @PostMapping("/submit", consumes = [APPLICATION_JSON_VALUE])
    suspend fun submit(@Valid @RequestBody request: SubmitRequest) =
        shortURLGenerator.generate(URI.create(request.url), request.shortCode)
            .let { SubmitResponse(request.url, it.toString()) }

}

sealed interface Validations {
    interface SecondOrder
}

@GroupSequence(value = [SubmitRequest::class, Validations.SecondOrder::class])
data class SubmitRequest(
    val url: String,

    @field:Length(min = 4, message = "property shortCode must be at least 4 characters long")
    @field:Pattern(
        regexp = "^\\w+$",
        message = "property shortCode can only contain numbers and letters",
        groups = [Validations.SecondOrder::class]
    )
    val shortCode: String?,
)

data class SubmitResponse(
    val url: String,
    val shortUrl: String
)
