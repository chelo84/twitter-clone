package com.github.twitterclone.sdk.domain.follow

import java.time.LocalDateTime

class Follow {
    var id: String? = null

    lateinit var pair: FollowPair

    var followDate: LocalDateTime? = null
}