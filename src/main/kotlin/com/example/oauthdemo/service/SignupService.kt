package com.example.oauthdemo.service

import com.example.oauthdemo.exception.UserAlreadyExistsException
import com.example.oauthdemo.model.document.user.User
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
                        userRepository.save(User().apply {
                            this.username = user.username
                            this.password = passwordEncoder.encode(user.password)
                            this.email = user.email
                            this.name = user.name
                        })
                    } else {
                        Mono.error(UserAlreadyExistsException(user.username))
                    }
                }
    }
}
