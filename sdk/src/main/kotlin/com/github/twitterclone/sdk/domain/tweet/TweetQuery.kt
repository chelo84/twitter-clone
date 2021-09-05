package com.github.twitterclone.sdk.domain.tweet

import com.github.twitterclone.sdk.domain.Query
import javax.validation.constraints.NotBlank

class TweetQuery(
    @NotBlank
    val username: String,
    page: Int,
    size: Int,
) : Query(page = page, size = size)