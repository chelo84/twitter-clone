package com.example.oauthdemo.repository.follow

import com.example.oauthdemo.model.document.follow.Follow
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface FollowRepository : ReactiveMongoRepository<Follow, String> {
    fun existsByPair_FollowerAndPair_Followed(follower: String, followed: String): Mono<Boolean>
}
