package com.example.oauthdemo.model.dto.user

import java.time.LocalDateTime
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class UserDto {
    var id: String? = null

    @NotBlank
    @Size(max = 100)
    var username: String? = null

    @NotBlank
    var password: String? = null

    @NotBlank
    @Size(max = 100)
    var name: String? = null

    @Email
    var email: String? = null

    var createdDate: LocalDateTime? = null

    var lastModifiedDate: LocalDateTime? = null
}