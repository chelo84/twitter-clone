package com.github.twitterclone.client.rsocket.factory

import com.github.twitterclone.client.rsocket.RSocketRequesterWrapper

/**
 * Repository containing all [RSocketReqFactory] available in application's context.
 *
 * used to dispose all everytime a user signs-in or signs-out.
 */
class RSocketReqFactoryRepository(private val rsocketReqFactories: List<RSocketReqFactory<*, *>>) {

    /**
     * Dispose all [RSocketRequesterWrapper]
     *
     * Called when a new authentication is made
     */
    fun disposeAll() {
        rsocketReqFactories.onEach { it.dispose() }
    }
}