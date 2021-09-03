package com.github.twitterclone.client.security

import com.github.twitterclone.client.rsocket.RSocketRequesterFactory
import com.github.twitterclone.client.rsocket.RSocketRequesterRepository
import com.github.twitterclone.sdk.domain.user.User
import io.rsocket.metadata.WellKnownMimeType
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.util.MimeTypeUtils
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

class ClientRemoteAuthenticationProvider(
    private val webClient: WebClient,
    private val rSocketRequesterFactory: RSocketRequesterFactory,
    private val rSocketRequesterRepository: RSocketRequesterRepository,
) : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.principal
        val password = authentication.credentials
        return webClient.post()
            .uri("/signin")
            .bodyValue(mapOf(Pair("username", username), Pair("password", password)))
            .exchangeToMono { resp ->
                if (resp.statusCode() == HttpStatus.OK) {
                    Mono.just(
                        resp.headers().header(HttpHeaders.AUTHORIZATION)[0]!!.replaceFirst(
                            "bearer ",
                            "",
                            ignoreCase = true
                        )
                    )
                } else if (resp.statusCode() == HttpStatus.UNAUTHORIZED) {
                    Mono.error(BadCredentialsException("Failed to authenticate"))
                } else {
                    Mono.error(InternalAuthenticationServiceException("Failed to reach authentication server"))
                }
            }
            .zipWhen { token ->
                getPrincipal(token)
            }
            .map { tuple: Tuple2<String, User> ->
                ClientAuthenticationToken(
                    tuple.t2,
                    tuple.t1
                )
            }
            .switchIfEmpty(Mono.error(Exception("empty")))
            .doOnSuccess { rSocketRequesterRepository.disposeAll() }
            .onErrorResume { e -> Mono.error(BadCredentialsException(e.message)) }
            .block()!!
    }

    override fun supports(authentication: Class<*>?): Boolean = true

    private fun getPrincipal(token: String): Mono<User> {
        return rSocketRequesterFactory.createRSocketRequester()
            .route("profile.user")
            .metadata(
                BearerTokenMetadata(token),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .retrieveMono(User::class.java)
    }

}