package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.user.User
import org.springframework.messaging.handler.annotation.MessageMapping

class TweetsHandler(private val shellHelper: ShellHelper, args: Map<out HandlerArgument, Any>) :
    Handler(shellHelper, args) {
    val username: String

    enum class TweetsArgument(override val value: String) : HandlerArgument {
        USERNAME("username")
    }

    init {
        this.username = args[TweetsArgument.USERNAME] as String? ?: throw Exception("username is required")
    }

    @MessageMapping("follow")
    fun follow(payload: User) {
        shellHelper.printWarning("User <${payload.username}> just followed you!!", above = true)
    }

    @MessageMapping("unfollow")
    fun unfollow(payload: User) {
        shellHelper.printWarning("User <${payload.username}> unfollowed you D:", above = true)
    }
}