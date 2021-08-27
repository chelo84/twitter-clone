package com.github.twitterclone.security.authentication

import com.github.twitterclone.security.jwt.JWTTokenService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import reactor.core.publisher.Mono

class LoginAuthenticationSuccessHandler : ServerAuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
            webFilterExchange: WebFilterExchange,
            authentication: Authentication,
    ): Mono<Void> {
        // Create and attach a JWT before passing the exchange to the filter chain
        val exchange = webFilterExchange.exchange
        exchange.response.statusCode = HttpStatus.ACCEPTED
        exchange.response
                .headers
                .add(HttpHeaders.AUTHORIZATION, getHttpAuthHeaderValue(authentication))
        return webFilterExchange.chain.filter(exchange)
    }

    private fun getHttpAuthHeaderValue(authentication: Authentication): String {
        return java.lang.String.join(" ", "Bearer", tokenFromAuthentication(authentication))
    }

    private fun tokenFromAuthentication(authentication: Authentication): String {
        return JWTTokenService.generateToken(
            authentication.name,
            authentication.authorities
        )
    }
}