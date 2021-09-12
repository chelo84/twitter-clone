package com.github.twitterclone.api.repository.tweet

import com.github.twitterclone.api.model.document.Tweet
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TweetNonReactiveRepository : MongoRepository<Tweet, String> {
    fun findAllByReplyTo(id: String): List<Tweet>
}