package com.example.twitterclone.model.dto.error

import java.time.LocalDateTime

data class ErrorDto(
        val timestamp: LocalDateTime,
        val status: Int,
        val error: String,
        val message: String,
        val path: String?,
)