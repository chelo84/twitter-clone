package com.example.oauthdemo.service

import com.example.oauthdemo.exception.UserAlreadyExistsException
import com.example.oauthdemo.model.document.User
import com.example.oauthdemo.repository.user.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SignupService(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
) {

    fun signup(user: User): Mono<User> {
        return userRepository.findByUsername(user.username)
                .hasElement()
                .flatMap { isPresent ->
                    if (!isPresent) {
                        userRepository.save(user.apply {
                            password = passwordEncoder.encode(password)
                        })
                    } else {
                        Mono.error(UserAlreadyExistsException(user.username))
                    }
                }
    }
}
