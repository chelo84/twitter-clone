package com.github.twitterclone.client.rsocket.factory

import com.github.twitterclone.client.rsocket.handler.DefaultProperties
import com.github.twitterclone.client.rsocket.handler.FollowHandler
import com.github.twitterclone.client.shell.ShellHelper
import org.springframework.stereotype.Component

@Component
class FollowRSocketReqFactory : RSocketReqFactory<FollowHandler, DefaultProperties>() {
    override fun createHandler(shellHelper: ShellHelper, properties: DefaultProperties): FollowHandler {
        return FollowHandler(shellHelper, properties)
    }
}