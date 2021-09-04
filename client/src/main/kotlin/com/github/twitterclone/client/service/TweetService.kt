package com.github.twitterclone.client.service

import com.github.twitterclone.client.rsocket.factory.DefaultRSocketReqFactory
import com.github.twitterclone.client.rsocket.factory.TweetRSocketReqFactory
import com.github.twitterclone.client.rsocket.handler.TweetProperties
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.tweet.NewTweet
import com.github.twitterclone.sdk.domain.tweet.Tweet
import com.github.twitterclone.sdk.domain.user.User
import io.rsocket.metadata.WellKnownMimeType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class TweetService(
    private val shellHelper: ShellHelper,
    private val tweetRSocketReqFactory: TweetRSocketReqFactory,
    private val defaultRSocketReqFactory: DefaultRSocketReqFactory,
) {
    /**
     * Observe new tweets from a [username]
     * @param username [User.username]
     */
    fun connectToTweets(username: String) {
        shellHelper.printInfo("Subscribing to tweets from user $username")
        val rsocketRequesterWrapper = tweetRSocketReqFactory.disposeAndCreate(TweetProperties(username))
        rsocketRequesterWrapper
            .rsocketRequester
            .route("tweets.$username")
            .metadata(
                BearerTokenMetadata(SecurityContextHolder.getContext().authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .sendMetadata()
            .doOnSuccess {
                val handler = rsocketRequesterWrapper.handler
                handler.getTweets()
                    .subscribe { tweet ->
                        shellHelper.printWarning("User <${tweet.user?.username}> tweeted: ${tweet.text}",
                                                 above = true)
                    }
            }
            .onErrorResume { shellHelper.printError("Couldn't unfollow: ${it.message}", above = true).toMono().then() }
            .subscribe()
    }

    fun isConnectedToUser(username: String): Boolean {
        return tweetRSocketReqFactory.getHandler()?.let { it.properties.username == username } ?: false
    }

    fun postTweet(text: String) {
        defaultRSocketReqFactory.get()
            .rsocketRequester
            .route("tweet")
            .metadata(
                BearerTokenMetadata(SecurityContextHolder.getContext().authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(NewTweet(text))
            .retrieveMono(Tweet::class.java)
            .doOnSuccess { shellHelper.printSuccess("Successfully posted new tweet!", above = true) }
            .onErrorResume {
                shellHelper.printError("Couldn't post tweet: ${it.message}", above = true).toMono()
                    .then(Mono.empty())
            }
            .subscribe()
    }
}