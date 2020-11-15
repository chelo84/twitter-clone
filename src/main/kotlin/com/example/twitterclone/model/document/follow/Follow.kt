package com.example.twitterclone.model.document.follow

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@Document
class Follow(val pair: FollowPair) {
    @Id
    var id: String? = null

    @NotNull
    @CreatedDate
    var followDate: LocalDateTime? = null
}