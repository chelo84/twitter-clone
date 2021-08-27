package com.github.twitterclone.security.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.io.IOException

class LoginBodyAuthConverter(private val mapper: ObjectMapper) : (ServerWebExchange) -> Mono<Authentication> {
    override fun invoke(exchange: ServerWebExchange): Mono<Authentication> {
        return exchange.request.body
                .next()
                .flatMap { buffer ->
                    try {
                        val request: AuthenticationData = mapper.readValue(buffer.asInputStream(),
                                                                           AuthenticationData::class.java)
                        Mono.just<AuthenticationData>(request)
                    } catch (e: IOException) {
                        Mono.error(e)
                    }
                }
                .map { UsernamePasswordAuthenticationToken(it.username, it.password) }

    }

    private data class AuthenticationData(
            var username: String,
            var password: String,
    )
}