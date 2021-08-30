package com.github.twitterclone.client.rsocket

import com.github.twitterclone.client.rsocket.handler.FollowHandler
import com.github.twitterclone.client.rsocket.handler.Handler
import com.github.twitterclone.client.shell.ShellHelper

enum class RSocketRequesterName(
    val createHandler: (ShellHelper) -> Handler,
) {
    FOLLOW({ shellHelper -> FollowHandler(shellHelper) })
}