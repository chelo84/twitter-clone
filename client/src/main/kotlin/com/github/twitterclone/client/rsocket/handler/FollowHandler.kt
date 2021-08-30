package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.user.User
import org.springframework.messaging.handler.annotation.MessageMapping
import reactor.core.publisher.Mono

class FollowHandler(private val shellHelper: ShellHelper) : Handler(shellHelper) {

    @MessageMapping("follow")
    fun follow(payload: User): Mono<Void> {
        shellHelper.printInfo("${payload.name} ${payload.surname} just followed you!!")
//        followedBy = payload
        return Mono.empty()
    }

    @MessageMapping("follow")
    fun unfollow(payload: User): Mono<Void> {
        shellHelper.printInfo("${payload.name} ${payload.surname} unfollowed you D:")
//        unfollowedBy = payload
        return Mono.empty()
    }
}