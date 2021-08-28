package com.github.twitterclone.sdk.domain

import com.github.twitterclone.sdk.domain.user.User
import java.time.LocalDateTime

class Post {
    lateinit var uid: String

    lateinit var text: String

    var createdDate: LocalDateTime? = null

    var lastModifiedDate: LocalDateTime? = null

    var createdBy: User? = null

    var lastModifiedBy: User? = null
}
