package com.github.twitterclone.client.command

import com.github.twitterclone.client.rsocket.RSocketRequesterFactory
import com.github.twitterclone.client.rsocket.RSocketRequesterName
import com.github.twitterclone.client.rsocket.handler.TweetsHandler
import com.github.twitterclone.client.rsocket.handler.TweetsHandler.TweetsArgument.USERNAME
import com.github.twitterclone.client.shell.ShellHelper
import io.rsocket.metadata.WellKnownMimeType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import org.springframework.util.MimeTypeUtils


@ShellComponent
class TweetsCommand(
    private val shellHelper: ShellHelper,
    private val rSocketRequesterFactory: RSocketRequesterFactory,
) : SecuredCommand() {

    @ShellMethod(value = "Get tweets from a user")
    fun tweets(@ShellOption(value = ["--username", "-u"], help = "User's username") username: String) {
        shellHelper.printInfo("Getting tweets from $username ...")
        val handler = rSocketRequesterFactory.getHandler(RSocketRequesterName.TWEETS) as TweetsHandler?
        if (handler?.username != username.trim()) {
            shellHelper.printWarning("username not equals")
            subscribeToTweets(username)
        }

        shellHelper.printInfo("... Find tweets ...")
    }

    private fun subscribeToTweets(username: String) {
        shellHelper.printInfo("Subscribing to tweets from user $username")
        rSocketRequesterFactory.get(RSocketRequesterName.TWEETS, mapOf(Pair(USERNAME, username)))
            .route("tweets", username)
            .metadata(
                BearerTokenMetadata(SecurityContextHolder.getContext().authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .sendMetadata()
            .retry()
            .subscribe()
    }
}