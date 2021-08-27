package com.github.twitterclone.model.dto.tweet

import com.github.twitterclone.model.dto.QueryDto
import javax.validation.constraints.NotBlank

class TweetQueryDto(
    @field:NotBlank
    val username: String,
    page: Int,
    size: Int,
) : QueryDto(page = page, size = size)