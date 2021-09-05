package com.github.twitterclone.server.model.document

import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.PositiveOrZero

@Document
data class Hashtag(
    val hashtag: String,
    @PositiveOrZero
    val startsAt: Int,
    @PositiveOrZero
    val endsAt: Int,
)
