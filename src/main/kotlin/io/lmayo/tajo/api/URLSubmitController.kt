package io.lmayo.tajo.api

import io.lmayo.tajo.domain.Code
import io.lmayo.tajo.domain.ShortURLService
import jakarta.validation.GroupSequence
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.URL
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI


@RestController
class URLSubmitController(
    private val shortURLGenerator: ShortURLService
) {

    @PostMapping("/submit", consumes = [APPLICATION_JSON_VALUE])
    suspend fun submit(@Valid @RequestBody request: SubmitRequest) =
        shortURLGenerator.generate(URI.create(request.url), request.shortCode?.let { Code(it) })
            ?.let { SubmitResponse(request.url, it.toString()) }
            ?: ResponseEntity.unprocessableEntity().body(
                ApplicationError(
                    listOf(ApiError("unable to generate short url"))
                )
            )

}

sealed interface Validations {
    interface SecondOrder
}

@GroupSequence(value = [SubmitRequest::class, Validations.SecondOrder::class])
data class SubmitRequest(
    @field:NotBlank(message = "property url must be present")
    @field:URL(message = "property url must be a valid URL")
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
