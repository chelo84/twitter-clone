package com.github.twitterclone.client.rsocket

import com.github.twitterclone.client.rsocket.handler.FollowHandler
import com.github.twitterclone.client.rsocket.handler.Handler
import com.github.twitterclone.client.rsocket.handler.HandlerArgument
import com.github.twitterclone.client.rsocket.handler.TweetsHandler
import com.github.twitterclone.client.shell.ShellHelper

enum class RSocketRequesterName(
    val createHandler: (ShellHelper, args: Map<out HandlerArgument, Any>) -> Handler,
) {
    FOLLOW(::FollowHandler),
    TWEETS(::TweetsHandler)
}