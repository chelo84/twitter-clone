package com.example.oauthdemo.security

import com.example.oauthdemo.repository.user.UserRepository
import com.example.oauthdemo.security.authentication.LoginAuthenticationSuccessHandler
import com.example.oauthdemo.security.bearer.BearerTokenReactiveAuthenticationManager
import com.example.oauthdemo.security.bearer.ServerHttpBearerAuthenticationConverter
import com.example.oauthdemo.security.service.CustomReactiveUserDetailsService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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
import java.io.IOException


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
    fun securityContextRepository(): ServerSecurityContextRepository? {
        val securityContextRepository = WebSessionServerSecurityContextRepository()
        securityContextRepository.setSpringSecurityContextAttrName("securityContext")
        return securityContextRepository
    }

    @Bean
    fun authenticationManager(): ReactiveAuthenticationManager {
        val authenticationManager = UserDetailsRepositoryReactiveAuthenticationManager(CustomReactiveUserDetailsService(
                passwordEncoder(),
                userRepository))
        authenticationManager.setPasswordEncoder(passwordEncoder())
        return authenticationManager
    }

    @Bean
    fun authenticationWebFilter(mapper: ObjectMapper): AuthenticationWebFilter {
        val filter = AuthenticationWebFilter(authenticationManager())
        filter.setSecurityContextRepository(securityContextRepository())
        filter.setServerAuthenticationConverter(LoginJsonAuthConverter(mapper))
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/signin", "/login")
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
                .pathMatchers("/login", "/logout", "/")
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

    private fun bearerAuthenticationFilter(): AuthenticationWebFilter? {
        val bearerAuthenticationFilter: AuthenticationWebFilter
        val bearerConverter: (ServerWebExchange) -> Mono<Authentication>
        val authManager: ReactiveAuthenticationManager
        authManager = BearerTokenReactiveAuthenticationManager()
        bearerAuthenticationFilter = AuthenticationWebFilter(authManager)
//        bearerAuthenticationFilter.setAuthenticationSuccessHandler(BearerAuthen)
        bearerConverter = serverHttpBearerAuthenticationConverter
        bearerAuthenticationFilter.setServerAuthenticationConverter(bearerConverter)
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"))
        return bearerAuthenticationFilter
    }
//    @Bean
//    fun oauth2UserService() = CustomReactiveOAuth2UserService()

//    @Bean
//    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
//        return http.authorizeExchange()
//                .anyExchange().authenticated()
//                .and().oauth2Login()
//                .and().build()
//    }

//    @Bean
//    fun webClient(
//            clientRegistrationRepo: ReactiveClientRegistrationRepository?,
//            authorizedClientRepo: ServerOAuth2AuthorizedClientRepository?,
//    ): WebClient {
//        val filter = ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepo, authorizedClientRepo)
//        return WebClient.builder().filter(filter).build()
//    }
}

data class AuthenticationData(
        var username: String,
        var password: String,
)

class LoginJsonAuthConverter(private val mapper: ObjectMapper) : (ServerWebExchange) -> Mono<Authentication> {
    override fun invoke(exchange: ServerWebExchange): Mono<Authentication> {
        return exchange.request.body
                .next()
                .flatMap { buffer: DataBuffer ->
                    try {
                        val request: AuthenticationData = mapper.readValue(buffer.asInputStream(),
                                                                           AuthenticationData::class.java)
                        Mono.just<AuthenticationData>(request)
                    } catch (e: IOException) {
//                        log.debug("Can't read login request from JSON")
                        Mono.error(e)
                    }
                }
                .map { request: AuthenticationData ->
                    UsernamePasswordAuthenticationToken(
                            request.username,
                            request.password
                    )
                }
    }
}