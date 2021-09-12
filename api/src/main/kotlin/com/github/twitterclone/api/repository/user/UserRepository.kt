package com.github.twitterclone.api.repository.user

import com.github.twitterclone.api.model.document.user.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveMongoRepository<User, String> {
    fun findByUsername(username: String): Mono<User>
}