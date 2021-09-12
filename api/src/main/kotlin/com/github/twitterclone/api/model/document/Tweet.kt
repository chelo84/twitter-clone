package com.github.twitterclone.api.model.document

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime
import java.util.*
import javax.validation.constraints.NotNull

@Document
class Tweet {

    @MongoId(FieldType.STRING)
    var uid: String? = UUID.randomUUID().toString()

    @NotNull
    var text: String = ""

    var createdDate: LocalDateTime? = null

    var lastModifiedDate: LocalDateTime? = null

    @NotNull
    var user: String? = null

    var hashtags: List<Hashtag> = listOf()

    var replyTo: String? = null
}