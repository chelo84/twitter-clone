package com.github.twitterclone.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.twitterclone.api.mapper.UserMapper
import com.github.twitterclone.api.service.SignupService
import com.github.twitterclone.sdk.domain.user.NewUser
import com.github.twitterclone.sdk.domain.user.User
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/signup")
class SignupController(
    private val signupService: SignupService,
    private val userMapper: UserMapper,
    private val mapper: ObjectMapper
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(uriComponentsBuilder: UriComponentsBuilder, @RequestBody @Valid userDto: NewUser): Mono<User> {
        return signupService.signup(userMapper.newUserToUser(userDto))
            .map(userMapper::userToDto)
    }
}