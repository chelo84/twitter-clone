package com.example.twitterclone.model.dto.tweet

import com.example.twitterclone.model.dto.QueryDto

class TweetQueryDto(
        val username: String,
        page: Int,
        size: Int,
) : QueryDto(page = page, size = size)