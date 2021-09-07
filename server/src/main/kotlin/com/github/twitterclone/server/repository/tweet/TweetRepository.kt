package com.github.twitterclone.server.repository.tweet

import com.github.twitterclone.server.model.document.Tweet
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TweetRepository : ReactiveMongoRepository<Tweet, String>, ReactiveSortingRepository<Tweet, String> {

    fun findAllByUserOrderByCreatedDateDesc(user: String, pageable: Pageable): Flux<Tweet>

    fun findAllByUidLike(id: String): Flux<Tweet>
}