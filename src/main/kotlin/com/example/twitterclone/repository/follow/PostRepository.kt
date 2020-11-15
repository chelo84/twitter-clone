package com.example.twitterclone.repository.follow

import com.example.twitterclone.model.document.Post
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : ReactiveMongoRepository<Post, String>