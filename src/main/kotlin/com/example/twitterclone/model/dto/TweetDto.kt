package com.example.twitterclone.model.dto

import com.example.twitterclone.model.document.Hashtag
import com.example.twitterclone.model.dto.user.UserDto
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

class TweetDto {

    @NotBlank
    var text: String = ""

    var createdDate: LocalDateTime? = null

    var createdBy: UserDto? = null

    var hashtags: List<Hashtag>? = listOf()
}
