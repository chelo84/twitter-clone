package com.github.twitterclone.server.service

import com.github.twitterclone.server.model.document.user.User
import com.github.twitterclone.server.repository.user.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ProfileService(private val userRepository: UserRepository) {

    fun getUser(principal: User): Mono<User> {
        return userRepository.findById(principal.id!!)
    }
}