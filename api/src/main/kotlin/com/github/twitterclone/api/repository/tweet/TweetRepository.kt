package com.github.twitterclone.api.repository.tweet

import com.github.twitterclone.api.model.document.Tweet
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TweetRepository : ReactiveMongoRepository<Tweet, String>, ReactiveSortingRepository<Tweet, String> {

    fun findAllByUserAndReplyToIsNullOrderByCreatedDateDesc(user: String, pageable: Pageable): Flux<Tweet>

    fun findAllByUidLike(id: String): Flux<Tweet>
}