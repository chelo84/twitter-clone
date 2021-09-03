package com.github.twitterclone.client.rsocket

import com.github.twitterclone.client.rsocket.RSocketRequesterRepository.Companion.rsocketRequesters
import com.github.twitterclone.client.rsocket.handler.Handler
import com.github.twitterclone.client.rsocket.handler.HandlerArgument
import org.springframework.messaging.rsocket.RSocketRequester

class RSocketRequesterRepository(
    private val rsocketRequesterFactory: RSocketRequesterFactory,
) {

    companion object {
        private val rsocketRequesters: MutableMap<RSocketRequesterName, RSocketRequesterWrapper> = mutableMapOf()
    }

    /**
     * Find or create [RSocketRequesterWrapper]
     *
     * If it yet does not exist, create a new one
     *
     * @param name [rsocketRequesters] key
     * @param args Arguments used in the constructor of the [Handler] for the [name]
     * @return [RSocketRequesterWrapper]
     */
    fun get(name: RSocketRequesterName, args: Map<out HandlerArgument, Any> = emptyMap()): RSocketRequesterWrapper {
        if (rsocketRequesters[name]?.rsocketRequester == null) {
            rsocketRequesters[name] = rsocketRequesterFactory.createRSocketRequesterWrapper(name, args)
        }
        return rsocketRequesters[name]!!
    }

    /**
     * Find or create [RSocketRequester]
     *
     * If it yet does not exist, create a new one
     *
     * @param name [rsocketRequesters] key
     * @param args Arguments used in the constructor of the [Handler] for the [name]
     * @return [RSocketRequester]
     */
    fun getRSocketRequester(
        name: RSocketRequesterName,
        args: Map<out HandlerArgument, Any> = emptyMap(),
    ): RSocketRequester {
        return get(name, args).rsocketRequester
    }

    /**
     * Dispose [RSocketRequester] with [name] and then creates a new [RSocketRequester]
     *
     * @param name [rsocketRequesters] key
     * @param args Arguments used in the constrcutor of the [Handler] of the [Handler]
     * @return [RSocketRequester]
     */
    fun disposeAndCreate(
        name: RSocketRequesterName,
        args: Map<out HandlerArgument, Any> = emptyMap(),
    ): RSocketRequesterWrapper {
        dispose(name)
        return get(name, args)
    }

    /**
     * Find [Handler] if existent
     * @param name [rsocketRequesters] key
     * @return nullable [Handler]
     */
    fun getHandler(name: RSocketRequesterName): Handler? {
        return rsocketRequesters[name]?.handler
    }

    /**
     * Dispose all [rsocketRequesters] and clear
     *
     * Called when a new authentication is made
     */
    fun disposeAll() {
        rsocketRequesters.keys.onEach(::dispose)
    }

    /**
     * Dispose [RSocketRequester] and its [Handler]
     */
    fun dispose(name: RSocketRequesterName) {
        rsocketRequesters[name]?.dispose()
        rsocketRequesters.remove(name)
    }

}