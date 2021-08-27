package com.github.twitterclone.security.rsocket

import com.github.twitterclone.config.Log
import com.github.twitterclone.security.bearer.ServerHttpBearerAuthenticationConverter
import com.github.twitterclone.security.converter.UserProfileAuthenticationConverter
import com.github.twitterclone.security.jwt.JWTSecrets
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.cbor.Jackson2CborDecoder
import org.springframework.http.codec.cbor.Jackson2CborEncoder
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
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
import org.springframework.security.rsocket.metadata.BearerTokenAuthenticationEncoder
import org.springframework.validation.beanvalidation.SpringValidatorAdapter
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.validation.Validation


@Configuration
@EnableRSocketSecurity
class RSocketSecurityConfig {
    companion object : Log()

    @Autowired
    private lateinit var serverHttpBearerAuthenticationConverter: ServerHttpBearerAuthenticationConverter

    @Bean
    fun messageHandler(
        rSocketStrategies: RSocketStrategies,
        springValidatorAdapter: SpringValidatorAdapter,
    ): RSocketMessageHandler {
        val messageHandler = RSocketMessageHandler()

        messageHandler.rSocketStrategies = rSocketStrategies

        val args: ArgumentResolverConfigurer = messageHandler
            .argumentResolverConfigurer
        args.addCustomResolver(AuthenticationPrincipalArgumentResolver())

        messageHandler.validator = springValidatorAdapter
        return messageHandler
    }

    @Bean
    fun rSocketStrategies(): RSocketStrategies {
        val rSocketStrategies = RSocketStrategies.create()
        return rSocketStrategies.mutate()
            .encoders { encoders ->
                encoders.add(Jackson2CborEncoder())
                encoders.add(Jackson2JsonEncoder())
                encoders.add(BearerTokenAuthenticationEncoder())
            }
            .decoders { decoders ->
                decoders.add(Jackson2CborDecoder())
                decoders.add(Jackson2JsonDecoder())
            }
            .build()
    }

    @Bean
    fun springValidatorAdapter(): SpringValidatorAdapter {
        return SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().validator)
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