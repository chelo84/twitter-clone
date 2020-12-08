package com.example.twitterclone.service

import com.example.twitterclone.model.document.user.User
import com.example.twitterclone.model.dto.user.UserDto
import com.example.twitterclone.repository.user.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ProfileService(private val userRepository: UserRepository) {

    fun getUser(principal: User): Mono<User> {
        return userRepository.findById(principal.id!!)
    }
}