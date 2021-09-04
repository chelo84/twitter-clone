package com.github.twitterclone.client.rsocket.factory

import com.github.twitterclone.client.rsocket.handler.TweetHandler
import com.github.twitterclone.client.rsocket.handler.TweetProperties
import com.github.twitterclone.client.shell.ShellHelper
import org.springframework.stereotype.Component

@Component
class TweetRSocketReqFactory : RSocketReqFactory<TweetHandler, TweetProperties>() {
    override fun createHandler(shellHelper: ShellHelper, properties: TweetProperties): TweetHandler {
        return TweetHandler(shellHelper, properties)
    }
}