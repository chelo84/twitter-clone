package com.example.oauthdemo.model.document

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@Document
class User {
    @Id
    var id: String? = null

    @NotNull
    lateinit var username: String

    @NotNull
    lateinit var password: String

    var email: String? = null

    @NotNull
    var name: String? = null

    @NotNull
    @CreatedDate
    var createdDate: LocalDateTime? = null

    @NotNull
    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null
}