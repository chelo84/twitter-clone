package com.example.twitterclone.controller

import com.example.twitterclone.mapper.UserMapper
import com.example.twitterclone.model.dto.user.UserDto
import com.example.twitterclone.service.SignupService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/signup")
class SignupController(private val signupService: SignupService, private val userMapper: UserMapper) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(uriComponentsBuilder: UriComponentsBuilder, @RequestBody @Valid userDto: UserDto): Mono<UserDto> {
        return signupService.signup(userMapper.dtoToUser(userDto))
                .map(userMapper::userToDto)
    }
}