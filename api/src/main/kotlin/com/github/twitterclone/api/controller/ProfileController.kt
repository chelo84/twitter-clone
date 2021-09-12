package com.github.twitterclone.api.controller

import com.github.twitterclone.api.config.Log
import com.github.twitterclone.api.mapper.UserMapper
import com.github.twitterclone.api.model.document.user.User
import com.github.twitterclone.api.service.ProfileService
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