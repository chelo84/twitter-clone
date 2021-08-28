package com.github.twitterclone.server.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.twitterclone.server.repository.user.UserRepository
import com.github.twitterclone.server.security.authentication.LoginAuthenticationSuccessHandler
import com.github.twitterclone.server.security.authentication.LoginBodyAuthConverter
import com.github.twitterclone.server.security.bearer.BearerTokenReactiveAuthenticationManager
import com.github.twitterclone.server.security.bearer.ServerHttpBearerAuthenticationConverter
import com.github.twitterclone.server.security.service.CustomReactiveUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var serverHttpBearerAuthenticationConverter: ServerHttpBearerAuthenticationConverter

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(11)
    }

    @Bean
    fun securityContextRepository(): ServerSecurityContextRepository {
        val securityContextRepository = WebSessionServerSecurityContextRepository()
        securityContextRepository.setSpringSecurityContextAttrName("securityContext")
        return securityContextRepository
    }

    @Bean
    fun authenticationManager(): ReactiveAuthenticationManager {
        val authenticationManager = UserDetailsRepositoryReactiveAuthenticationManager(
            CustomReactiveUserDetailsService(
                userRepository
            )
        )
        authenticationManager.setPasswordEncoder(passwordEncoder())
        return authenticationManager
    }

    @Bean
    fun authenticationWebFilter(mapper: ObjectMapper): AuthenticationWebFilter {
        val filter = AuthenticationWebFilter(authenticationManager())
        filter.setSecurityContextRepository(securityContextRepository())
        filter.setServerAuthenticationConverter(LoginBodyAuthConverter(mapper))
        filter.setRequiresAuthenticationMatcher(
            ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/signin")
        )
        filter.setAuthenticationSuccessHandler(LoginAuthenticationSuccessHandler())
        return filter
    }

    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity,
        mapper: ObjectMapper,
    ): SecurityWebFilterChain {
        http
            .csrf().disable()
            .authorizeExchange()
            .pathMatchers("/signup")
            .permitAll()
            .and()
            .authorizeExchange()
            .pathMatchers("/signin", "/signout")
            .authenticated()
            .and()
            .addFilterAt(authenticationWebFilter(mapper), SecurityWebFiltersOrder.AUTHENTICATION)
            .authorizeExchange()
            .pathMatchers("/api/**")
            .authenticated()
            .and()
            .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
        return http.build()
    }

    private fun bearerAuthenticationFilter(): AuthenticationWebFilter {
        val authManager: ReactiveAuthenticationManager = BearerTokenReactiveAuthenticationManager()
        val bearerAuthenticationFilter = AuthenticationWebFilter(authManager)
        val bearerConverter: (ServerWebExchange) -> Mono<Authentication> = serverHttpBearerAuthenticationConverter
        bearerAuthenticationFilter.setServerAuthenticationConverter(bearerConverter)
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"))
        return bearerAuthenticationFilter
    }
}