package com.github.twitterclone.client.rsocket

import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.user.User
import io.rsocket.SocketAcceptor
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class RSocketRequesterFactory(
    private val strategies: RSocketStrategies,
    private val builder: RSocketRequester.Builder,
    private val shellHelper: ShellHelper,

    ) {

    companion object {
        private val rSocketRequesters: MutableMap<Pair<String, RSocketRequesterName>, RSocketRequester> = mutableMapOf()
    }

    fun getForName(name: RSocketRequesterName): RSocketRequester {
        val principal = SecurityContextHolder.getContext().authentication.principal as User
        val rSocketRequester: RSocketRequester = rSocketRequesters[Pair(principal.id, name)]
            ?: createRSocketRequester(name.createHandler(shellHelper))
        rSocketRequesters[Pair(principal.id!!, name)] = createRSocketRequester(name.createHandler(shellHelper))

        return rSocketRequester
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
            .rsocketConnector { if (responder != null) it.acceptor(responder) }
            .tcp("localhost", 7000)
    }
}