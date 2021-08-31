package com.github.twitterclone.client.rsocket

import com.github.twitterclone.client.rsocket.handler.Handler
import com.github.twitterclone.client.rsocket.handler.HandlerArgument
import com.github.twitterclone.client.shell.ShellHelper
import io.rsocket.SocketAcceptor
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.stereotype.Component
import reactor.util.retry.Retry
import java.time.Duration

@Component
class RSocketRequesterFactory(
    private val strategies: RSocketStrategies,
    private val builder: RSocketRequester.Builder,
    private val shellHelper: ShellHelper,

    ) {

    companion object {
        private val rsocketRequesters: MutableMap<RSocketRequesterName, Pair<RSocketRequester, Handler>> =
            mutableMapOf()
    }

    fun get(name: RSocketRequesterName, args: Map<out HandlerArgument, Any> = emptyMap()): RSocketRequester {
        val rsocketRequester: RSocketRequester =
            rsocketRequesters[name]?.first ?: run {
                val handler = name.createHandler(shellHelper, args)
                val rsocketRequester = createRSocketRequester(handler)
                rsocketRequesters[name] = Pair(rsocketRequester, handler)

                rsocketRequester
            }

        return rsocketRequester
    }

    fun getHandler(name: RSocketRequesterName): Handler? {
        return rsocketRequesters[name]?.second
    }

    /**
     * Used when a new authentication is made
     * Disposes all [RSocketRequester.rsocketClient] and then clear [rsocketRequesters]
     */
    fun disposeAll() {
        rsocketRequesters
            .onEach { it.value.first.rsocketClient().dispose() }
            .clear()
    }

    fun createRSocketRequester(): RSocketRequester {
        return createRSocketRequester(null)
    }

    fun createRSocketRequester(handler: Any?): RSocketRequester {
        var responder: SocketAcceptor? = null
        if (handler != null) {
            responder = RSocketMessageHandler.responder(strategies, handler)
        }
        return builder
            .rsocketConnector { connector ->
                responder?.let { connector.acceptor(it) }

                connector.reconnect(Retry.backoff(10, Duration.ofMillis(500)))
            }
            .tcp("localhost", 7000)
    }
}