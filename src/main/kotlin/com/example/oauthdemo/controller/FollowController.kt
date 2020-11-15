package com.example.oauthdemo.controller

import com.example.oauthdemo.model.document.follow.Follow
import com.example.oauthdemo.model.document.user.User
import com.example.oauthdemo.service.FollowService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


@Controller
class FollowController(
        private val followService: FollowService,
) {

    private val connectedClients: ConcurrentMap<String, RSocketRequester> = ConcurrentHashMap()

    @MessageMapping("follow")
    fun hello(@AuthenticationPrincipal principal: User, id: String): Mono<Follow> {
        return followService.follow(principal, id)
    }

    @MessageMapping("unfollow")
    fun helloSecure(name: String?): Mono<String>? {
        TODO()
    }
}