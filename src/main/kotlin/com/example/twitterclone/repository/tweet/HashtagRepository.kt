package com.example.twitterclone.repository.tweet

import com.example.twitterclone.model.document.Hashtag
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface HashtagRepository : ReactiveMongoRepository<Hashtag, String> {

    fun findByHashtag(hashtag: String): Mono<Hashtag>
}