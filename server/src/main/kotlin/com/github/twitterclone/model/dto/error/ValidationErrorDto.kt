package com.github.twitterclone.model.dto.error

import java.time.LocalDateTime

data class ValidationErrorDto(
        val timestamp: LocalDateTime,
        val status: Int,
        val reason: String?,
        val errors: List<ValidationErrorFieldDto>,
        val path: String?,
)

data class ValidationErrorFieldDto(
        val field: String,
        val message: String?,
)