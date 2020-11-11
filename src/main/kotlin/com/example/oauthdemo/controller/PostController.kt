package com.example.oauthdemo.controller

import com.example.oauthdemo.config.Log
import com.example.oauthdemo.mapper.PostMapper
import com.example.oauthdemo.model.document.User
import com.example.oauthdemo.service.PostService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/posts")
class PostController(
        private val postService: PostService,
        private val postMapper: PostMapper,
) {
    companion object : Log();

    @GetMapping
    fun index(
            @AuthenticationPrincipal
            oauth2User: Mono<OAuth2User>,
    ): Mono<String> {
        return oauth2User
                .map(OAuth2User::getAttributes)
                .map { "Hi, $it" }
    }

    @PostMapping
    fun create(@AuthenticationPrincipal test: Mono<User>) = postService.create().map { postMapper.postToDto(it) }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String) = postService.find(id).map { postMapper.postToDto(it) }

}