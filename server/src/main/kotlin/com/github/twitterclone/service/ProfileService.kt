package com.github.twitterclone.service

import com.github.twitterclone.model.document.user.User
import com.github.twitterclone.model.dto.user.UserDto
import com.github.twitterclone.repository.user.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ProfileService(private val userRepository: UserRepository) {

    fun getUser(principal: User): Mono<User> {
        return userRepository.findById(principal.id!!)
    }
}