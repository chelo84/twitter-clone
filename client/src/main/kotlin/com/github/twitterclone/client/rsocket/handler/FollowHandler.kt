package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.user.User
import org.springframework.messaging.handler.annotation.MessageMapping

class FollowHandler(private val shellHelper: ShellHelper, args: Map<out HandlerArgument, Any>) :
    Handler(shellHelper, args) {

    /**
     * Called whenever a user follows the signed-in user
     * @param user: user that followed the signed-in user
     */
    @MessageMapping("follow")
    fun follow(user: User) {
        shellHelper.printWarning("User <${user.username}> just followed you!!", above = true)
    }

    /**
     * Called whenever a user unfollows the signed-in user
     * @param user: user that unfollowed the signed-in user
     */
    @MessageMapping("unfollow")
    fun unfollow(user: User) {
        shellHelper.printWarning("User <${user.username}> unfollowed you D:", above = true)
    }
}