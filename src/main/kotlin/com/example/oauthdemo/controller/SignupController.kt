package com.example.oauthdemo.controller

import com.example.oauthdemo.mapper.UserMapper
import com.example.oauthdemo.model.dto.UserDto
import com.example.oauthdemo.service.SignupService
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