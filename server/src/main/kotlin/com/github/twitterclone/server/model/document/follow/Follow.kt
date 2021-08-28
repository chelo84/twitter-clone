package com.github.twitterclone.server.model.document.follow

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@Document
class Follow() {

    @Id
    var id: String? = null

    lateinit var pair: FollowPair

    @NotNull
    @CreatedDate
    var followDate: LocalDateTime? = null

    constructor(pair: FollowPair) : this() {
        this.pair = pair
    }
}