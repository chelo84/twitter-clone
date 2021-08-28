package com.github.twitterclone.server.controller.handler

import com.github.twitterclone.sdk.domain.error.Error
import com.github.twitterclone.sdk.domain.error.ValidationErrorDto
import com.github.twitterclone.sdk.domain.error.ValidationErrorFieldDto
import com.github.twitterclone.server.config.Log
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.client.HttpStatusCodeException
import java.time.LocalDateTime


@ControllerAdvice
@RestController
class ApiValidatorExceptionHandler {
    companion object : Log();

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception, request: ServerHttpRequest): Any? {
        log.error(ex.message, ex)
        val httpStatus = this.getHttpStatus(ex)
        val errorDetails = Error(
            timestamp = LocalDateTime.now(),
            status = httpStatus.value(),
            error = httpStatus.reasonPhrase,
            message = ex.message ?: "",
            path = request.path.toString()
        )
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(ex: WebExchangeBindException, request: ServerHttpRequest): Any? {
        val httpStatus = this.getHttpStatus(ex)
        val errorDetails = ValidationErrorDto(
            timestamp = LocalDateTime.now(),
            status = httpStatus.value(),
            reason = ex.reason,
            errors = ex.fieldErrors.map { ValidationErrorFieldDto(it.field, it.defaultMessage) },
            path = request.path.toString()
        )
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    fun getHttpStatus(ex: Exception) = when (ex) {
        is HttpStatusCodeException -> ex.statusCode
        else -> HttpStatus.INTERNAL_SERVER_ERROR
    }
}