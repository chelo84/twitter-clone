package com.example.oauthdemo.service

import com.example.oauthdemo.exception.UserAlreadyFollowedException
import com.example.oauthdemo.model.document.follow.Follow
import com.example.oauthdemo.model.document.follow.FollowPair
import com.example.oauthdemo.model.document.user.User
import com.example.oauthdemo.repository.follow.FollowRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FollowService {

    @Autowired
    private lateinit var followRepository: FollowRepository

    fun follow(principal: User, followed: String): Mono<Follow> {
        return followRepository.existsByPair_FollowerAndPair_Followed(
                principal.id!!,
                followed
        ).flatMap { exists ->
            if (exists)
                Mono.error(UserAlreadyFollowedException())
            else
                newFollow(principal.id!!, followed)
        }.flatMap(followRepository::save)
    }

    private fun newFollow(follower: String, followed: String): Mono<Follow> {
        return Mono.just(Follow(
                FollowPair(
                        follower = follower,
                        followed = followed
                )
        ))
    }
}
