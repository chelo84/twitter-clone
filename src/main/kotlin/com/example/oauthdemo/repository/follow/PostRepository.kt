package com.example.oauthdemo.repository.follow

import com.example.oauthdemo.model.document.Post
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : ReactiveMongoRepository<Post, String>