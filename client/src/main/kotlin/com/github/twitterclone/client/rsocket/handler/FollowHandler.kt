package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.command.FollowCommand
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.user.User
import org.springframework.messaging.handler.annotation.MessageMapping
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

class FollowHandler(private val shellHelper: ShellHelper, args: Map<out HandlerArgument, Any>) :
    Handler(shellHelper, args) {

    private val follows: Sinks.Many<User> = Sinks.many().multicast().directBestEffort()
    fun getFollows(): Flux<User> = follows.asFlux()
    private val unfollows: Sinks.Many<User> = Sinks.many().multicast().directBestEffort()
    fun getUnfollows(): Flux<User> = unfollows.asFlux()

    /**
     * Called whenever a user follows the signed-in user
     * @param user user that followed
     * @see [FollowCommand.follow]
     */
    @MessageMapping("follow")
    fun follow(user: User) {
        follows.tryEmitNext(user)
    }

    /**
     * Called whenever a user unfollows the signed-in user
     * @param user user that unfollowed
     * @see [FollowCommand.unfollow]
     */
    @MessageMapping("unfollow")
    fun unfollow(user: User) {
        unfollows.tryEmitNext(user)
    }

    override fun dispose() {
        shellHelper.printInfo("DISPOSE FOLLOW")

        follows.tryEmitComplete()
        unfollows.tryEmitComplete()
    }
}