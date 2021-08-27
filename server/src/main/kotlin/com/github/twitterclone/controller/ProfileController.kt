package com.github.twitterclone.controller

import com.github.twitterclone.config.Log
import com.github.twitterclone.mapper.UserMapper
import com.github.twitterclone.model.document.user.User
import com.github.twitterclone.model.dto.user.UserDto
import com.github.twitterclone.service.ProfileService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono

@Controller
internal class ProfileController(
    private val profileService: ProfileService,
    private val userMapper: UserMapper
) {
    companion object : Log()

    @MessageMapping("profile.user")
    fun userProfile(@AuthenticationPrincipal principal: User): Mono<UserDto> {
        return profileService.getUser(principal)
            .map(userMapper::userToDto)
    }
}