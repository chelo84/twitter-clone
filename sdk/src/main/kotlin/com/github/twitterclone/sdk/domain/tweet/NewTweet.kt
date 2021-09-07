package com.github.twitterclone.sdk.domain.tweet

import javax.validation.constraints.NotBlank

data class NewTweet(
    @NotBlank
    val text: String,
    val replyTo: String? = null,
)