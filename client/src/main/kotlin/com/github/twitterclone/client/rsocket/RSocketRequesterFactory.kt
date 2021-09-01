package com.github.twitterclone.client.rsocket

import com.github.twitterclone.client.rsocket.RSocketRequesterFactory.Companion.rsocketRequesters
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

    /**
     * Get [RSocketRequester] from [RSocketRequesterName]
     * If none exist, create a new [RSocketRequester]
     */
    fun get(name: RSocketRequesterName, args: Map<out HandlerArgument, Any> = emptyMap()): RSocketRequester =
        rsocketRequesters[name]?.first ?: createRSocketRequester(name, args)

    /**
     * Dispose [RSocketRequester] from [RSocketRequesterName] and then creates a new [RSocketRequester]
     */
    fun disposeAndCreate(
        name: RSocketRequesterName,
        args: Map<out HandlerArgument, Any> = emptyMap(),
    ): RSocketRequester {
        dispose(name)
        return get(name, args)
    }

    /**
     * Get [Handler] from [RSocketRequesterName]
     */
    fun getHandler(name: RSocketRequesterName): Handler? {
        return rsocketRequesters[name]?.second
    }

    /**
     * Used when a new authentication is made
     * Disposes all [RSocketRequester.rsocketClient] and clear [rsocketRequesters]
     */
    fun disposeAll() {
        rsocketRequesters.keys
            .onEach(::dispose)
    }

    /**
     * Dispose [RSocketRequester] from [RSocketRequesterName]
     */
    fun dispose(name: RSocketRequesterName) {
        rsocketRequesters[name]?.first?.rsocketClient()?.dispose()
        rsocketRequesters[name]?.second?.dispose()

        rsocketRequesters.remove(name)
    }

    /**
     * Creates a new [RSocketRequester] from [RSocketRequesterName] creating its [Handler]
     */
    private fun createRSocketRequester(
        name: RSocketRequesterName,
        args: Map<out HandlerArgument, Any> = emptyMap(),
    ): RSocketRequester {
        val handler = name.createHandler(shellHelper, args)
        val rsocketRequester = createRSocketRequester(handler)
        rsocketRequesters[name] = Pair(rsocketRequester, handler)

        return rsocketRequester
    }

    fun createRSocketRequester(): RSocketRequester = createRSocketRequester(null)

    fun createRSocketRequester(handler: Any?): RSocketRequester {
        val responder: SocketAcceptor? = handler?.let { RSocketMessageHandler.responder(strategies, handler) }
        return builder
            .rsocketConnector { connector ->
                responder?.let { connector.acceptor(responder) }
                connector.reconnect(Retry.backoff(10, Duration.ofMillis(500)))
            }
            .tcp("localhost", 7000)
    }
}