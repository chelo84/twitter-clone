package com.github.twitterclone.client.rsocket

import com.github.twitterclone.client.rsocket.handler.*
import com.github.twitterclone.client.shell.ShellHelper

enum class RSocketRequesterName(
    val createHandler: (ShellHelper, args: Map<out HandlerArgument, Any>) -> Handler,
) {
    DEFAULT(::DefaultHandler),
    FOLLOW(::FollowHandler),
    TWEETS(::TweetsHandler)
}