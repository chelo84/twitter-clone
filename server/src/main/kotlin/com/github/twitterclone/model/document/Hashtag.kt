package com.github.twitterclone.model.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.NotNull

@Document
class Hashtag(
        @Id
        @NotNull
        @Indexed(unique = true)
        val hashtag: String,
)
