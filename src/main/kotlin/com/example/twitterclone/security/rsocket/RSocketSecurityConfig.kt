package com.example.twitterclone.security.rsocket

import com.example.twitterclone.config.Log
import com.example.twitterclone.security.bearer.ServerHttpBearerAuthenticationConverter
import com.example.twitterclone.security.converter.UserProfileAuthenticationConverter
import com.example.twitterclone.security.jwt.JWTSecrets
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.handler.invocation.reactive.ArgumentResolverConfigurer
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity.AuthorizePayloadsSpec
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


@Configuration
@EnableRSocketSecurity
class RSocketSecurityConfig {
    companion object : Log()

    @Autowired
    private lateinit var serverHttpBearerAuthenticationConverter: ServerHttpBearerAuthenticationConverter

    @Bean
    fun messageHandler(rSocketStrategies: RSocketStrategies): RSocketMessageHandler {
        val messageHandler: RSocketMessageHandler = RSocketMessageHandler()
        messageHandler.rSocketStrategies = rSocketStrategies
        val args: ArgumentResolverConfigurer = messageHandler
                .argumentResolverConfigurer
        args.addCustomResolver(AuthenticationPrincipalArgumentResolver())
        return messageHandler
    }

    @Bean
    fun rsocketInterceptor(
            rsocket: RSocketSecurity,
            jwtReactiveAuthenticationManager: JwtReactiveAuthenticationManager,
    ): PayloadSocketAcceptorInterceptor {
        rsocket.authorizePayload { authorize: AuthorizePayloadsSpec ->
            authorize
                    .anyRequest()
                    .authenticated()
                    .anyExchange()
                    .permitAll()
        }.jwt { jwtSpec: RSocketSecurity.JwtSpec ->
            try {
                jwtSpec.authenticationManager(jwtReactiveAuthenticationManager)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        return rsocket.build()
    }

    @Bean
    fun reactiveJwtDecoder(): ReactiveJwtDecoder {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(JWTSecrets.DEFAULT_SECRET.toByteArray(), mac.algorithm)
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build()
    }

    @Bean
    fun jwtReactiveAuthenticationManager(
            reactiveJwtDecoder: ReactiveJwtDecoder,
            converter: UserProfileAuthenticationConverter,
    ): JwtReactiveAuthenticationManager {
        val jwtReactiveAuthenticationManager = JwtReactiveAuthenticationManager(reactiveJwtDecoder)
        val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_")
        jwtReactiveAuthenticationManager.setJwtAuthenticationConverter(converter)
        return jwtReactiveAuthenticationManager
    }
}