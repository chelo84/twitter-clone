package com.github.twitterclone.sdk.domain.error

import java.time.LocalDateTime

data class Error(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String?,
)