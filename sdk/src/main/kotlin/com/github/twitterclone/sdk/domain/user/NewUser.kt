package com.github.twitterclone.sdk.domain.user

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class NewUser(
    @NotBlank
    @Size(max = 100)
    val username: String,
    @NotBlank
    val password: String,
    @NotBlank
    val passwordConfirmation: String,
    @NotBlank
    @Size(max = 100)
    val name: String,
    @NotBlank
    @Size(max = 100)
    val surname: String,
    @Email
    val email: String,
)