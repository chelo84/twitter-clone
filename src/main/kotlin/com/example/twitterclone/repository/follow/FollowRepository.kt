package com.example.twitterclone.repository.follow

import com.example.twitterclone.model.document.follow.Follow
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface FollowRepository : ReactiveMongoRepository<Follow, String> {
    fun findByPair_FollowerAndPair_Followed(follower: String, followed: String): Mono<Follow?>
}