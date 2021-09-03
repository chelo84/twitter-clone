package com.github.twitterclone.client.rsocket

import com.github.twitterclone.client.rsocket.handler.Handler
import org.springframework.messaging.rsocket.RSocketRequester
import reactor.core.Disposable

data class RSocketRequesterWrapper(
    val rsocketRequester: RSocketRequester,
    val handler: Handler,
) : Disposable {

    override fun dispose() {
        rsocketRequester.rsocketClient().dispose()
        handler.dispose()
    }
}