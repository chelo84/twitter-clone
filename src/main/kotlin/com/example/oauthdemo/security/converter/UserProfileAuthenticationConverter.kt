package com.example.oauthdemo.security.converter

import com.example.oauthdemo.model.document.user.User
import com.example.oauthdemo.repository.user.UserRepository
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserProfileAuthenticationConverter(private val repository: UserRepository) : Converter<Jwt, Mono<AbstractAuthenticationToken>?> {
    private val converter: JwtAuthenticationConverter = JwtAuthenticationConverter()

    override fun convert(source: Jwt): Mono<AbstractAuthenticationToken> {
        val token: JwtAuthenticationToken = converter.convert(source) as JwtAuthenticationToken
        val username = source.getClaim<String>("sub")
        return repository.findByUsername(username)
                .switchIfEmpty(Mono.error { UsernameNotFoundException("Couldn't find the user $username") })
                .map { profile -> UserProfileAuthentication(token, profile) }
    }

    class UserProfileAuthentication constructor(
            private val token: JwtAuthenticationToken,
            private val principal: User,
    ) : AbstractAuthenticationToken(token.authorities) {
        init {
            isAuthenticated = true
            details = principal
        }

        override fun getCredentials(): Any {
            return token
        }

        override fun getPrincipal(): Any {
            return principal
        }

    }
}
