package com.example.twitterclone.controller

import com.example.twitterclone.config.Log
import com.example.twitterclone.mapper.UserMapper
import com.example.twitterclone.model.document.follow.Follow
import com.example.twitterclone.model.document.user.User
import com.example.twitterclone.service.FollowService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


@Controller
class FollowController(
        private val followService: FollowService,
        private val userMapper: UserMapper,
) {
    companion object : Log() {
        const val FOLLOW: String = "follow"
        const val UNFOLLOW: String = "unfollow"
    }

    private val requesterMap: ConcurrentMap<String, RSocketRequester> = ConcurrentHashMap()

    @MessageMapping(FOLLOW)
    fun followStream(@AuthenticationPrincipal principal: User, id: String): Flux<String> {
        return Flux.from { listOf(id) }
    }

    @ConnectMapping(FOLLOW)
    fun connect(@AuthenticationPrincipal principal: User, rSocketRequester: RSocketRequester) {
        GlobalScope.launch {
            rSocketRequester.rsocket()!!
                    .onClose()
                    .subscribe(null, null) { requesterMap.remove(principal.id!!, rSocketRequester) }
        }

        requesterMap[principal.id!!] = rSocketRequester
    }

    @MessageMapping(FOLLOW)
    fun follow(@AuthenticationPrincipal principal: User, id: String): Mono<Follow> {
        return followService.follow(principal, id)
                .doOnNext {
                    requesterMap[id]?.route(FOLLOW)
                            ?.data(userMapper.findAndMapUserToUserDto(it.pair.follower))
                            ?.send()
                            ?.subscribe()
                }
    }

    @MessageMapping(UNFOLLOW)
    fun unfollow(@AuthenticationPrincipal principal: User, id: String): Mono<Void> {
        return followService.unfollow(principal, id)
                .switchIfEmpty {
                    requesterMap[id]?.route(UNFOLLOW)
                            ?.data(userMapper.findAndMapUserToUserDto(principal.id!!))
                            ?.send()
                            ?.subscribe()
                    Mono.empty()
                }
    }
}