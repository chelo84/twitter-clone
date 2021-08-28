package com.github.twitterclone.server.security.bearer

import com.github.twitterclone.server.repository.user.UserNonReactiveRepository
import com.github.twitterclone.server.repository.user.UserRepository
import com.nimbusds.jwt.SignedJWT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.stream.Collectors
import java.util.stream.Stream

@Component
class UsernamePasswordAuthenticationBearer {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userNonReactiveRepository: UserNonReactiveRepository

    fun create(signedJWTMono: SignedJWT): Mono<Authentication> {
        val authorities = getAuthorities(signedJWTMono) ?: return Mono.empty()

        return userRepository.findByUsername(signedJWTMono.jwtClaimsSet.subject as String)
            .map { UsernamePasswordAuthenticationToken(it, null, authorities) }
    }


    private fun getAuthorities(signedJWTMono: SignedJWT): List<GrantedAuthority>? {
        return try {
            val auths = signedJWTMono.jwtClaimsSet.getClaim("roles") as String
            Stream.of(auths.split(","))
                .flatMap { it.stream() }
                .map(::SimpleGrantedAuthority)
                .collect(Collectors.toList())
        } catch (ex: Exception) {
            null
        }
    }
}