package com.github.twitterclone.server.security.bearer

import com.github.twitterclone.server.security.jwt.JWTCustomVerifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class ServerHttpBearerAuthenticationConverter : (ServerWebExchange) -> Mono<Authentication> {
    private val jwtVerifier: JWTCustomVerifier = JWTCustomVerifier()

    @Autowired
    private lateinit var usernamePasswordAuthenticationBearer: UsernamePasswordAuthenticationBearer

    companion object {
        private const val BEARER = "Bearer "
        private val matchBearerLength: (String) -> Boolean = { authValue -> authValue.length > BEARER.length }
        private val isolateBearerValue: (String) -> Mono<String> = { authValue ->
            Mono.justOrEmpty(authValue.substring(BEARER.length))
        }
    }

    /**
     * Apply this function to the current WebExchange, an Authentication object
     * is returned when completed.
     *
     * @param serverWebExchange
     * @return
     */
    override fun invoke(serverWebExchange: ServerWebExchange): Mono<Authentication> {
        return Mono.justOrEmpty(serverWebExchange)
            .flatMap(AuthorizationHeaderPayload::extract)
            .filter(matchBearerLength)
            .flatMap(isolateBearerValue)
            .flatMap(jwtVerifier::check)
            .flatMap(usernamePasswordAuthenticationBearer::create)
    }

    private object AuthorizationHeaderPayload {
        fun extract(serverWebExchange: ServerWebExchange): Mono<String> {
            return Mono.justOrEmpty(
                serverWebExchange.request
                    .headers
                    .getFirst(HttpHeaders.AUTHORIZATION)
            )
        }
    }
}