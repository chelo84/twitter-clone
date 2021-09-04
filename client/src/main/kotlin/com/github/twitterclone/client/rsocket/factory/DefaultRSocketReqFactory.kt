package com.github.twitterclone.client.rsocket.factory

import com.github.twitterclone.client.rsocket.handler.DefaultHandler
import com.github.twitterclone.client.rsocket.handler.DefaultProperties
import com.github.twitterclone.client.shell.ShellHelper
import org.springframework.stereotype.Component

@Component
class DefaultRSocketReqFactory : RSocketReqFactory<DefaultHandler, DefaultProperties>() {
    override fun createHandler(shellHelper: ShellHelper, properties: DefaultProperties): DefaultHandler {
        return DefaultHandler(shellHelper, properties)
    }
}