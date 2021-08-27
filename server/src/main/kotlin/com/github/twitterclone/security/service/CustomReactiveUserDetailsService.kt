package com.github.twitterclone.security.service

import com.github.twitterclone.repository.user.UserRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CustomReactiveUserDetailsService(
    private val userRepository: UserRepository,
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository.findByUsername(username)
            .map {
                User.builder()
                    .username(it.username)
                    .password(it.password)
                    .roles("USER")
                    .build()
            }
    }
}