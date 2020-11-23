package com.example.twitterclone.model.document

import org.springframework.data.annotation.*
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@Document
class Tweet {

    @Id
    var uid: String? = null

    @NotNull
    var text: String = ""

    @NotNull
    @CreatedDate
    var createdDate: LocalDateTime? = null

    @NotNull
    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null

    @NotNull
    @CreatedBy
    var createdBy: String? = null

    @DBRef
    var hashtags: List<Hashtag>? = listOf()
}