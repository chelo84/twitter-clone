package com.example.oauthdemo.service

import com.example.oauthdemo.model.document.User
import com.example.oauthdemo.model.security.UserInfo
import com.example.oauthdemo.repository.user.UserNonReactiveRepository
import com.example.oauthdemo.repository.user.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
        private val userRepository: UserRepository,
        private val userNonReactiveRepository: UserNonReactiveRepository,
) {
    fun create(userInfo: UserInfo): Mono<User> {
        return Mono.empty()
//        val user = this.newUser(userInfo)
//        return this.find(user.sub, user.email)
//                .hasElement()
//                .flatMap { isPresent ->
//                    if (isPresent)
//                        Mono.error(UserAlreadyExistsException(user.sub, user.email))
//                    else
//                        userRepository.save(user)
//                }
    }

    fun createNonReactive(userInfo: UserInfo): User? {
        return null
//        val user = this.newUser(userInfo)
//
//        this.findNonReactive(user.sub, user.email)?.let { throw UserAlreadyExistsException(user.sub, user.email) }
//        return userNonReactiveRepository.save(user)
    }

    private fun newUser(userInfo: UserInfo): User = User().apply {
//        sub = userInfo.sub
//        email = userInfo.email
//        name = userInfo.name
    }

    fun find(sub: String, email: String): Mono<User> {
        return Mono.empty()
//        return userRepository.findByUsername(sub, email)
    }

    // TODO remove this?
//    fun findNonReactive(sub: String, email: String): User? {
//        return userNonReactiveRepository.findByUsername(email)
//    }
}