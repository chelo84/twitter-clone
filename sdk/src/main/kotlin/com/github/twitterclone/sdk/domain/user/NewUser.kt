package com.github.twitterclone.sdk.domain.user

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class NewUser(
    var id: String? = null,

    @NotBlank
    @Size(max = 100)
    var username: String? = null,

    @NotBlank
    var password: String? = null,

    @NotBlank
    var passwordConfirmation: String? = null,

    @NotBlank
    @Size(max = 100)
    var name: String? = null,

    @NotBlank
    @Size(max = 100)
    var surname: String? = null,

    @Email
    var email: String? = null,
)