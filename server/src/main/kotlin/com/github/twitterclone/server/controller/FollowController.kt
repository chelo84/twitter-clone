package com.github.twitterclone.server.controller

import com.github.twitterclone.sdk.domain.follow.Follow
import com.github.twitterclone.server.config.Log
import com.github.twitterclone.server.mapper.FollowMapper
import com.github.twitterclone.server.mapper.UserMapper
import com.github.twitterclone.server.model.document.user.User
import com.github.twitterclone.server.service.FollowService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


@Controller
class FollowController(
    private val followService: FollowService,
    private val userMapper: UserMapper,
    private val followMapper: FollowMapper,
) {
    companion object : Log() {
        const val FOLLOW: String = "follow"
        const val UNFOLLOW: String = "unfollow"
    }

    private val requesterMap: ConcurrentMap<String, RSocketRequester> = ConcurrentHashMap()

    @ConnectMapping(FOLLOW)
    fun connect(@AuthenticationPrincipal principal: User, rSocketRequester: RSocketRequester) {
        GlobalScope.launch {
            rSocketRequester.rsocket()!!
                .onClose()
                .subscribe(null, null) { requesterMap.remove(principal.username, rSocketRequester) }
        }

        requesterMap[principal.username] = rSocketRequester
    }

    @MessageMapping(FOLLOW)
    fun follow(@AuthenticationPrincipal principal: User, username: String): Mono<Follow> {
        return followService.follow(principal, username)
            .map(followMapper::followToDto)
            .doOnNext {
                requesterMap[username]?.route(FOLLOW)
                    ?.data(userMapper.findAndMapUserToUserDto(it.pair.follower))
                    ?.send()
                    ?.subscribe()
            }
    }

    @MessageMapping(UNFOLLOW)
    fun unfollow(@AuthenticationPrincipal principal: User, username: String): Mono<Void> {
        return followService.unfollow(principal, username)
            .switchIfEmpty {
                requesterMap[username]?.route(UNFOLLOW)
                    ?.data(userMapper.findAndMapUserToUserDto(principal.username))
                    ?.send()
                    ?.subscribe()
                Mono.empty()
            }
    }
}