package com.github.twitterclone.client.rsocket

import com.github.twitterclone.client.rsocket.handler.Handler
import org.springframework.messaging.rsocket.RSocketRequester
import reactor.core.Disposable

data class RSocketRequesterWrapper<T : Handler<*>>(
    val rsocketRequester: RSocketRequester,
    val handler: T,
) : Disposable {

    override fun dispose() {
        rsocketRequester.rsocketClient().dispose()
        handler.dispose()
    }
}