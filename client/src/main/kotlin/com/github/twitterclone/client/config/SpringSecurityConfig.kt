package com.github.twitterclone.client.config

import com.github.twitterclone.client.rsocket.factory.DefaultRSocketReqFactory
import com.github.twitterclone.client.rsocket.factory.RSocketReqFactoryRepository
import com.github.twitterclone.client.security.ClientRemoteAuthenticationProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class SpringSecurityConfig {

    @Bean
    fun authenticationProvider(
        webClient: WebClient,
        defaultRSocketReqFactory: DefaultRSocketReqFactory,
        rsocketReqFactoryRepository: RSocketReqFactoryRepository,
    ): AuthenticationProvider {
        return ClientRemoteAuthenticationProvider(webClient, defaultRSocketReqFactory, rsocketReqFactoryRepository)
    }
}