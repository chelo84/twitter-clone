package com.github.twitterclone.client.command

import com.github.twitterclone.client.rsocket.RSocketRequesterFactory
import com.github.twitterclone.client.rsocket.RSocketRequesterName
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.follow.Follow
import com.github.twitterclone.sdk.domain.user.User
import io.rsocket.metadata.WellKnownMimeType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import org.springframework.util.MimeTypeUtils
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


@ShellComponent
class FollowCommand(
    private val shellHelper: ShellHelper,
    private val rSocketRequesterFactory: RSocketRequesterFactory,
) : SecuredCommand() {

    /**
     * Follow a [username]
     *
     * This action notifies the [username] followed
     * @param username User's username
     */
    @ShellMethod(value = "Follow a user")
    fun follow(@ShellOption(value = ["--username", "-u"], help = "User's username to follow") username: String) {
        shellHelper.printInfo("following $username ...")
        rSocketRequesterFactory.get(RSocketRequesterName.FOLLOW)
            .route("follow")
            .metadata(
                BearerTokenMetadata(SecurityContextHolder.getContext().authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(username)
            .retrieveMono(Follow::class.java)
            .doOnNext { follow ->
                shellHelper.printSuccess("Successfully followed ${follow.pair.followed}",
                                         above = true)
            }
            .onErrorResume {
                shellHelper.printError("Couldn't follow: ${it.message}", above = true).toMono()
                    .then(Mono.empty())
            }
            .subscribe()
    }

    /**
     * Unfollow a [username]
     *
     * This action notifies the [username] unfollowed
     * @param username [User.username]
     */
    @ShellMethod(value = "Unfollow a user")
    fun unfollow(@ShellOption(value = ["--username", "-u"], help = "User's username to unfollow") username: String) {
        shellHelper.printInfo("unfollowing $username ...")
        rSocketRequesterFactory.get(RSocketRequesterName.FOLLOW)
            .route("unfollow")
            .metadata(
                BearerTokenMetadata(SecurityContextHolder.getContext().authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(username)
            .retrieveMono(Void::class.java)
            .doOnSuccess { shellHelper.printSuccess("Successfully unfollowed $username", above = true) }
            .onErrorResume { shellHelper.printError("Couldn't unfollow: ${it.message}", above = true).toMono().then() }
            .subscribe()
    }
}