package com.github.twitterclone.server.controller

import com.github.twitterclone.server.config.Log
import com.github.twitterclone.server.mapper.UserMapper
import com.github.twitterclone.server.model.document.user.User
import com.github.twitterclone.server.service.ProfileService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import com.github.twitterclone.sdk.domain.user.User as UserSdk

@Controller
internal class ProfileController(
    private val profileService: ProfileService,
    private val userMapper: UserMapper
) {
    companion object : Log()

    @MessageMapping("profile.user")
    fun userProfile(@AuthenticationPrincipal principal: User): Mono<UserSdk> {
        return profileService.getUser(principal)
            .map(userMapper::userToDto)
    }
}