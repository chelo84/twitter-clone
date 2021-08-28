package com.github.twitterclone.client.rsocket

import io.rsocket.SocketAcceptor
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.stereotype.Component

@Component
class RSocketRequesterFactory(
    private val strategies: RSocketStrategies,
    private val builder: RSocketRequester.Builder
) {

    fun createRSocketRequester(): RSocketRequester {
        return createRSocketRequester(null)
    }

    fun createRSocketRequester(handler: Any?): RSocketRequester {
        var responder: SocketAcceptor? = null
        if (handler != null) {
            responder = RSocketMessageHandler.responder(strategies, handler)
        }
        return builder
            .rsocketConnector { if (responder != null) it.acceptor(responder) }
            .tcp("localhost", 7000)
    }
}