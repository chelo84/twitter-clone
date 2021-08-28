package com.github.twitterclone.client.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.cbor.Jackson2CborDecoder
import org.springframework.http.codec.cbor.Jackson2CborEncoder
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.security.rsocket.metadata.BearerTokenAuthenticationEncoder

@Configuration
class RSocketConfig {

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
}
