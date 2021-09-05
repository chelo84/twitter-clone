package com.github.twitterclone.sdk.domain.user

import java.time.LocalDateTime

data class User(
    val id: String,
    val username: String,
    val name: String,
    val surname: String,
    val email: String,
    val createdDate: LocalDateTime,
    val lastModifiedDate: LocalDateTime,
)