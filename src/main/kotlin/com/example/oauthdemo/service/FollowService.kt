package com.example.oauthdemo.service

import com.example.oauthdemo.exception.UserAlreadyFollowedException
import com.example.oauthdemo.exception.UserNotFoundException
import com.example.oauthdemo.exception.UserToFollowItselfException
import com.example.oauthdemo.model.document.follow.Follow
import com.example.oauthdemo.model.document.follow.FollowPair
import com.example.oauthdemo.model.document.user.User
import com.example.oauthdemo.repository.follow.FollowRepository
import com.example.oauthdemo.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FollowService {

    @Autowired
    private lateinit var followRepository: FollowRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    fun follow(principal: User, followed: String): Mono<Follow> {
        return followRepository.findByPair_FollowerAndPair_Followed(principal.id!!,
                                                                    followed)
                .hasElement()
                .flatMap { exists ->
                    when {
                        exists -> Mono.error(UserAlreadyFollowedException())
                        principal.id!! == followed -> Mono.error(UserToFollowItselfException())
                        else -> newFollow(principal.id!!, followed)
                    }
                }
                .flatMap(followRepository::save)
    }

    private fun newFollow(follower: String, followed: String): Mono<Follow> {
        return userRepository.findById(followed)
                .hasElement()
                .flatMap { exists ->
                    if (exists) {
                        Mono.just(Follow(
                                FollowPair(
                                        follower = follower,
                                        followed = followed
                                )
                        ))
                    } else {
                        Mono.error(UserNotFoundException(followed))
                    }
                }
    }
}
