package com.github.twitterclone.sdk.domain.tweet

import com.github.twitterclone.sdk.domain.user.User
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

class Tweet {

    var uid: String? = null

    @NotBlank
    var text: String = ""

    var createdDate: LocalDateTime? = null

    var lastModifiedDate: LocalDateTime? = null

    var user: User? = null

    var hashtags: List<String> = listOf()
}
