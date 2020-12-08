package com.example.twitterclone.model.dto.tweet

import com.example.twitterclone.model.dto.user.UserDto
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

class TweetDto {

    var uid: String? = null

    @NotBlank
    var text: String = ""

    var createdDate: LocalDateTime? = null

    var lastModifiedDate: LocalDateTime? = null

    var user: UserDto? = null

    var hashtags: List<String> = listOf()
}
