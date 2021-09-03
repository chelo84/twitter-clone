package com.github.twitterclone.client.service

import com.github.twitterclone.client.rsocket.RSocketRequesterWrapper
import com.github.twitterclone.client.rsocket.handler.TweetsHandler
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.user.User
import io.rsocket.metadata.WellKnownMimeType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils

@Service
class TweetService(
    private val shellHelper: ShellHelper,
) {
    /**
     * Observe new tweets from a [username]
     * @param username [User.username]
     */
    fun connectToTweets(
        username: String,
        rsocketRequesterWrapper: RSocketRequesterWrapper,
    ) {
        shellHelper.printInfo("Subscribing to tweets from user $username")
        rsocketRequesterWrapper.rsocketRequester
            .route("tweets", username)
            .metadata(
                BearerTokenMetadata(SecurityContextHolder.getContext().authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .sendMetadata()
            .doOnSuccess {
                val handler = rsocketRequesterWrapper.handler as TweetsHandler
                handler.getTweets()
                    .subscribe { tweet ->
                        shellHelper.printWarning("User <${tweet.user?.username}> tweeted: ${tweet.text}!!",
                                                 above = true)
                    }
            }
            .subscribe()
    }
}