package com.example.twitterclone.repository.tweet

import com.example.twitterclone.model.document.Tweet
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TweetRepository : ReactiveMongoRepository<Tweet, String> {

    fun findAllByUser(user: String): Flux<Tweet>
}