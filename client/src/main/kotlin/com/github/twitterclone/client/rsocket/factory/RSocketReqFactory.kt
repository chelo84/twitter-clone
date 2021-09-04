package com.github.twitterclone.client.rsocket.factory

import com.github.twitterclone.client.rsocket.RSocketRequesterWrapper
import com.github.twitterclone.client.rsocket.handler.Handler
import com.github.twitterclone.client.rsocket.handler.HandlerFactory
import com.github.twitterclone.client.rsocket.handler.HandlerProperties
import com.github.twitterclone.client.shell.ShellHelper
import io.rsocket.SocketAcceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import reactor.util.retry.Retry
import java.time.Duration

/**
 * Factory for [RSocketRequesterWrapper] that contains [RSocketRequester] and [Handler].
 *
 * @param H implementation of [Handler].
 * @param P Properties used in the [Handler] constructor.
 */
abstract class RSocketReqFactory<H : Handler<P>, P : HandlerProperties> : HandlerFactory<H, P> {

    private var rsocketRequesterWrapper: RSocketRequesterWrapper<H>? = null

    @Autowired
    private lateinit var strategies: RSocketStrategies

    @Autowired
    private lateinit var builder: RSocketRequester.Builder

    @Autowired
    private lateinit var shellHelper: ShellHelper

    open fun get(): RSocketRequesterWrapper<H>? {
        return rsocketRequesterWrapper
    }

    /**
     * Find or create a [RSocketRequesterWrapper].
     *
     * @param properties [Handler] constructor arguments.
     * @return [RSocketRequesterWrapper].
     */
    fun getOrCreate(properties: P): RSocketRequesterWrapper<H> {
        if (rsocketRequesterWrapper?.rsocketRequester == null) {
            rsocketRequesterWrapper = createRSocketRequesterWrapper(properties)
        }
        return rsocketRequesterWrapper!!
    }

    /**
     * Dispose [RSocketRequesterWrapper] and then creates a new one.
     *
     * @param properties [Handler] constructor arguments
     * @return [RSocketRequesterWrapper]
     */
    fun disposeAndCreate(
        properties: P,
    ): RSocketRequesterWrapper<H> {
        dispose()
        return getOrCreate(properties)
    }

    /**
     * Find [Handler] if existent
     *
     * @return nullable [Handler]
     */
    fun getHandler(): Handler<P>? {
        return rsocketRequesterWrapper?.handler
    }

    /**
     * Dispose [RSocketRequesterWrapper]
     */
    fun dispose() {
        rsocketRequesterWrapper?.dispose()
        rsocketRequesterWrapper = null
    }

    /**
     * Create a new [RSocketRequesterWrapper] and its [Handler]
     *
     * @param properties [Handler] constructor arguments
     * @return [RSocketRequesterWrapper]
     */
    fun createRSocketRequesterWrapper(
        properties: P,
    ): RSocketRequesterWrapper<H> {
        val handler = createHandler(shellHelper, properties)
        return createRSocketRequesterWrapper(handler)
    }

    fun createRSocketRequesterWrapper(handler: H): RSocketRequesterWrapper<H> {
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