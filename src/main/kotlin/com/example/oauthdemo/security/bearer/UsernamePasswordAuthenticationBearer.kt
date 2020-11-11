package com.example.oauthdemo.security.bearer

import com.example.oauthdemo.repository.user.UserRepository
import com.nimbusds.jwt.SignedJWT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.stream.Collectors
import java.util.stream.Stream

@Component
class UsernamePasswordAuthenticationBearer {
    @Autowired
    private lateinit var userRepository: UserRepository

    fun create(signedJWTMono: SignedJWT): Mono<Authentication> {
        val authorities: List<Any>
        try {
            val auths = signedJWTMono.jwtClaimsSet.getClaim("roles") as String
            authorities = Stream.of(auths.split(","))
                    .flatMap { it.stream() }
                    .map { a -> SimpleGrantedAuthority(a) }
                    .collect(Collectors.toList())
        } catch (e: Exception) {
            return Mono.empty()
        }

        return userRepository.findByUsername(signedJWTMono.jwtClaimsSet.subject as String)
                .map { UsernamePasswordAuthenticationToken(it, null, authorities) }
    }
}