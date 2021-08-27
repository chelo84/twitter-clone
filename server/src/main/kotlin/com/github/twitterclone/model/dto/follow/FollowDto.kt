package com.github.twitterclone.model.dto.follow

import java.time.LocalDateTime

class FollowDto {
    var id: String? = null

    lateinit var pair: FollowPairDto

    var followDate: LocalDateTime? = null
}