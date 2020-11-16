package com.example.twitterclone.service

import com.example.twitterclone.exception.UserAlreadyFollowedException
import com.example.twitterclone.exception.UserNotFollowedException
import com.example.twitterclone.exception.UserNotFoundException
import com.example.twitterclone.exception.UserToFollowItselfException
import com.example.twitterclone.model.document.follow.Follow
import com.example.twitterclone.model.document.follow.FollowPair
import com.example.twitterclone.model.document.user.User
import com.example.twitterclone.repository.follow.FollowRepository
import com.example.twitterclone.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FollowService {

    @Autowired
    private lateinit var followRepository: FollowRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    fun follow(principal: User, userToFollow: String): Mono<Follow> {
        return followRepository.findByPair_FollowerAndPair_Followed(principal.id!!,
                                                                    userToFollow)
                .hasElement()
                .flatMap { exists ->
                    when {
                        exists -> Mono.error(UserAlreadyFollowedException())
                        principal.id!! == userToFollow -> Mono.error(UserToFollowItselfException())
                        else -> newFollow(principal.id!!, userToFollow)
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

    fun unfollow(principal: User, userToUnfollow: String): Mono<Void> {
        return userRepository.findById(userToUnfollow)
                .switchIfEmpty(Mono.error(UserNotFoundException(userToUnfollow)))
                .flatMap {
                    followRepository.findByPair_FollowerAndPair_Followed(principal.id!!,
                                                                         userToUnfollow)
                }
                .switchIfEmpty(Mono.error(UserNotFollowedException()))
                .flatMap { followRepository.deleteById(it!!.id!!) }
    }
}
