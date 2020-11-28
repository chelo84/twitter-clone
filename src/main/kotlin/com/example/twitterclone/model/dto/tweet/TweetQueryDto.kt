package com.example.twitterclone.model.dto.tweet

import com.example.twitterclone.model.dto.QueryDto
import javax.validation.constraints.NotBlank

class TweetQueryDto(
        @field:NotBlank
        val username: String,
        page: Int,
        size: Int,
) : QueryDto(page = page, size = size)