package com.example.twitterclone.model.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.NotNull

@Document
class Hashtag(
        @NotNull
        @Indexed(unique = true)
        val hashtag: String,
) {

    @Id
    var uid: String? = null
}
