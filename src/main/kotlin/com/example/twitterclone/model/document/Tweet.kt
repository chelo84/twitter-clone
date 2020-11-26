package com.example.twitterclone.model.document

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
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
    var user: String? = null

    var hashtags: List<String> = listOf()
}