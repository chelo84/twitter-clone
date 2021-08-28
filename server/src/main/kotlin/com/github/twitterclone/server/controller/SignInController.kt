package com.github.twitterclone.server.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/signin")
class SignInController {

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun signin(): ResponseEntity<Void> = ResponseEntity.ok().build()
}