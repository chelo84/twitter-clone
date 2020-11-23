package com.example.twitterclone.repository.tweet

import com.example.twitterclone.model.document.Tweet
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TweetRepository : ReactiveMongoRepository<Tweet, String>