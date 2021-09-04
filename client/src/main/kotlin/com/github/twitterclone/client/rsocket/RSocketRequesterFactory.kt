package com.github.twitterclone.client.rsocket

import com.github.twitterclone.client.rsocket.handler.Handler
import com.github.twitterclone.client.rsocket.handler.HandlerArgument
import com.github.twitterclone.client.shell.ShellHelper
import io.rsocket.SocketAcceptor
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import reactor.util.retry.Retry
import java.time.Duration

class RSocketRequesterFactory(
    private val strategies: RSocketStrategies,
    private val builder: RSocketRequester.Builder,
    private val shellHelper: ShellHelper,
) {


    /**
     * Create a new [RSocketRequester] from [name], also creates its [Handler] using [args]
     */
    fun createRSocketRequesterWrapper(
        name: RSocketRequesterName,
        args: Map<out HandlerArgument, Any> = emptyMap(),
    ): RSocketRequesterWrapper {
        val handler = name.createHandler(shellHelper, args)
        return createRSocketRequesterWrapper(handler)
    }

    fun createRSocketRequesterWrapper(handler: Handler): RSocketRequesterWrapper {
        val responder: SocketAcceptor = handler.let { RSocketMessageHandler.responder(strategies, handler) }
        return RSocketRequesterWrapper(
            createRSocketRequester(responder),
            handler
        )
    }

    fun createRSocketRequester(): RSocketRequester = createRSocketRequester(null)

    fun createRSocketRequester(responder: SocketAcceptor?): RSocketRequester {
        return builder
            .rsocketConnector { connector ->
                responder?.let { connector.acceptor(it) }
                connector.reconnect(Retry.backoff(10, Duration.ofMillis(500)))
            }
            .tcp("localhost", 7000)
    }
}