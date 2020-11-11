package com.example.oauthdemo.model.dto

import java.time.LocalDateTime

class PostDto {
    lateinit var uid: String

    lateinit var text: String

    var createdDate: LocalDateTime? = null

    var lastModifiedDate: LocalDateTime? = null

    var createdBy: UserDto? = null

    var lastModifiedBy: UserDto? = null
}
