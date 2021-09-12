package com.github.twitterclone.api.service

import com.github.twitterclone.api.model.document.user.User
import com.github.twitterclone.api.repository.user.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ProfileService(private val userRepository: UserRepository) {

    fun getUser(principal: User): Mono<User> {
        return userRepository.findById(principal.id!!)
    }
}