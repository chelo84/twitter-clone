package com.github.twitterclone.sdk.domain.tweet

import com.github.twitterclone.sdk.domain.user.User
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

class Tweet(
    val uid: String,
    @NotBlank
    var text: String = "",
    val createdDate: LocalDateTime,
    val lastModifiedDate: LocalDateTime?,
    val user: User,
    val hashtags: List<Hashtag> = listOf(),
    val replyTo: String?,
    val replies: List<Tweet> = listOf(),
)
