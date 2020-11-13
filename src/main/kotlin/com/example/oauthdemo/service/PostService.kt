package com.example.oauthdemo.service

import com.example.oauthdemo.exception.NotFoundException
import com.example.oauthdemo.model.document.Post
import com.example.oauthdemo.repository.follow.PostRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class PostService(private val postRepository: PostRepository) {
    fun create(): Mono<Post> {
        return postRepository.save(Post().apply {
            this.text = "Testing this thing"
        })
    }

    fun find(id: String): Mono<Post> {
        return postRepository.findById(id)
                .switchIfEmpty(Mono.error(NotFoundException("No post was found with id $id")))
    }
}