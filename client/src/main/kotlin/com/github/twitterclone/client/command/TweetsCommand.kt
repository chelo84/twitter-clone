package com.github.twitterclone.client.command

import com.github.twitterclone.client.command.state.TweetsState
import com.github.twitterclone.client.rsocket.RSocketRequesterFactory
import com.github.twitterclone.client.rsocket.RSocketRequesterName
import com.github.twitterclone.client.rsocket.handler.TweetsHandler
import com.github.twitterclone.client.rsocket.handler.TweetsHandler.TweetsArgument.USERNAME
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.user.User
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
    private val rsocketRequesterFactory: RSocketRequesterFactory,
    private val tweetsState: TweetsState,
) : SecuredCommand() {

    /**
     * Query tweets from [username]
     * @param username [User.username]. _Signed-in user if absent_
     * @param more whether to load more data. _Ignored if username is present_
     */
    @ShellMethod(value = "Get tweets from a user")
    fun tweets(
        @ShellOption(
            value = ["--username", "-u"],
            help = "User's username",
            defaultValue = ShellOption.NULL
        ) username: String?,
        @ShellOption(
            value = ["--more", "-m"],
            help = "Used to search for more tweets from the user (ignored if username is present)"
        ) more: Boolean,
    ) {
        shellHelper.printInfo("Getting tweets from $username (more? $more)...")

        username?.also {
            val handler = rsocketRequesterFactory.getHandler(RSocketRequesterName.TWEETS) as TweetsHandler?
            if (handler?.username != username.trim()) {
                shellHelper.printWarning("username not equals")
                subscribeToTweets(username)
            }
        } ?: run {
            rsocketRequesterFactory.dispose(RSocketRequesterName.TWEETS)
        }

        shellHelper.printInfo("... Find tweets ...")
    }

    /**
     * Observe new tweets from a [username]
     * @param username [User.username]
     */
    private fun subscribeToTweets(username: String) {
        shellHelper.printInfo("Subscribing to tweets from user $username")
        rsocketRequesterFactory.disposeAndCreate(RSocketRequesterName.TWEETS, mapOf(Pair(USERNAME, username)))
            .route("tweets", username)
            .metadata(
                BearerTokenMetadata(SecurityContextHolder.getContext().authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .sendMetadata()
            .doOnSuccess {
                val handler = rsocketRequesterFactory.getHandler(RSocketRequesterName.TWEETS) as TweetsHandler
                handler.getTweets()
                    .subscribe { tweet ->
                        shellHelper.printWarning("User <${tweet.user?.username}> tweeted: ${tweet.text}!!",
                                                 above = true)
                    }
            }
            .subscribe()
    }
}