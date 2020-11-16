package com.example.twitterclone.service

import com.example.twitterclone.exception.NotFoundException
import com.example.twitterclone.model.document.Post
import com.example.twitterclone.repository.follow.PostRepository
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