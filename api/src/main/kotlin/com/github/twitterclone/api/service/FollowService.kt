package com.github.twitterclone.api.service

import com.github.twitterclone.api.exception.UserAlreadyFollowedException
import com.github.twitterclone.api.exception.UserNotFollowedException
import com.github.twitterclone.api.exception.UserNotFoundException
import com.github.twitterclone.api.exception.UserToFollowItselfException
import com.github.twitterclone.api.model.document.follow.Follow
import com.github.twitterclone.api.model.document.follow.FollowPair
import com.github.twitterclone.api.model.document.user.User
import com.github.twitterclone.api.repository.follow.FollowRepository
import com.github.twitterclone.api.repository.user.UserRepository
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
        return followRepository.findByPair_FollowerAndPair_Followed(
            principal.username,
            userToFollow
        )
            .hasElement()
            .flatMap { exists ->
                when {
                    exists -> Mono.error(UserAlreadyFollowedException())
                    principal.username == userToFollow -> Mono.error(UserToFollowItselfException())
                    else -> newFollow(principal.username, userToFollow)
                }
            }
            .flatMap(followRepository::save)
    }

    private fun newFollow(follower: String, followed: String): Mono<Follow> {
        return userRepository.findByUsername(followed)
            .hasElement()
            .flatMap { exists ->
                if (exists) {
                    Mono.just(
                        Follow(
                            FollowPair(
                                follower = follower,
                                followed = followed
                            )
                        )
                    )
                } else {
                    Mono.error(UserNotFoundException(followed))
                }
            }
    }

    fun unfollow(principal: User, userToUnfollow: String): Mono<Void> {
        return userRepository.findByUsername(userToUnfollow)
            .switchIfEmpty(Mono.error(UserNotFoundException(userToUnfollow)))
            .flatMap {
                followRepository.findByPair_FollowerAndPair_Followed(
                    principal.username,
                    userToUnfollow
                )
            }
            .switchIfEmpty(Mono.error(UserNotFollowedException()))
            .flatMap { followRepository.deleteById(it!!.id!!) }
    }
}
