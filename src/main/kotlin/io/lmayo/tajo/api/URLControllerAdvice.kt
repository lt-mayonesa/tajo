package io.lmayo.tajo.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class URLControllerAdvice {

    @ExceptionHandler(WebExchangeBindException::class)
    fun handle(e: WebExchangeBindException): ResponseEntity<ApplicationError> =
        e.bindingResult.allErrors
            .map { it.defaultMessage ?: "${it.objectName} invalid format" }
            .map { ApiError(it) }
            .let { ApplicationError(it) }
            .let { ResponseEntity.badRequest().body(it) }

}