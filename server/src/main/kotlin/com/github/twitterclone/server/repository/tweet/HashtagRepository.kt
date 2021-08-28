package com.github.twitterclone.server.repository.tweet

import com.github.twitterclone.server.model.document.Hashtag
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface HashtagRepository : ReactiveMongoRepository<Hashtag, String> {

    fun findByHashtag(hashtag: String): Mono<Hashtag>
}