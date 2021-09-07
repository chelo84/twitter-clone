package com.github.twitterclone.client.service

import com.github.twitterclone.client.rsocket.factory.DefaultRSocketReqFactory
import com.github.twitterclone.client.rsocket.factory.TweetRSocketReqFactory
import com.github.twitterclone.client.rsocket.handler.TweetProperties
import com.github.twitterclone.client.shell.PromptColor
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.client.util.TemporalUtils
import com.github.twitterclone.sdk.domain.tweet.NewTweet
import com.github.twitterclone.sdk.domain.tweet.Tweet
import com.github.twitterclone.sdk.domain.tweet.TweetQuery
import com.github.twitterclone.sdk.domain.user.User
import io.rsocket.metadata.WellKnownMimeType
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStringBuilder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.format.DateTimeFormatter

@Service
class TweetService(
    private val shellHelper: ShellHelper,
    private val tweetRSocketReqFactory: TweetRSocketReqFactory,
    private val defaultRSocketReqFactory: DefaultRSocketReqFactory,
) {
    /**
     * Observe new tweets from a [username]
     *
     * @param username [User.username]
     */
    fun connectToTweets(username: String): Mono<Void> {
        shellHelper.printInfo("Subscribing to tweets from user $username")
        val rsocketRequesterWrapper = tweetRSocketReqFactory.disposeAndCreate(TweetProperties(username))
        return rsocketRequesterWrapper
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
                        if (tweet.replyTo == null) {
                            shellHelper.print("${
                                shellHelper.getColored("NEW TWEET", PromptColor.YELLOW).toAnsi()
                            } -> ${getTweetToPrint(tweet).toAnsi()}", above = true)
                        } else {
                            // TODO
                        }
                    }
            }
            .onErrorResume { shellHelper.printError("Couldn't unfollow: ${it.message}", above = true).toMono().then() }
    }

    fun isConnectedToUser(username: String): Boolean {
        return tweetRSocketReqFactory.getHandler()?.let { it.properties.username == username } ?: false
    }

    fun postTweet(text: String, replyTo: String?) {
        defaultRSocketReqFactory.get()
            .rsocketRequester
            .route("tweet")
            .metadata(
                BearerTokenMetadata(SecurityContextHolder.getContext().authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(NewTweet(text = text, replyTo = replyTo))
            .retrieveMono(Tweet::class.java)
            .doOnSuccess { shellHelper.printSuccess("Successfully posted new tweet!", above = true) }
            .onErrorResume {
                shellHelper.printError("Couldn't post tweet: ${it.message}", above = true).toMono()
                    .then(Mono.empty())
            }
            .subscribe()
    }

    fun fetchTweets(username: String, page: Int): Flux<Tweet> {
        return defaultRSocketReqFactory.get()
            .rsocketRequester
            .route("tweets")
            .metadata(
                BearerTokenMetadata(SecurityContextHolder.getContext().authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(
                TweetQuery(
                    username = username,
                    page = page,
                    size = 10
                )
            )
            .retrieveFlux(Tweet::class.java)
            .sort(Comparator.comparing(Tweet::createdDate))
    }

    fun getTweetToPrint(tweet: Tweet): AttributedString {
        val text = StringBuilder(tweet.text)
        tweet.hashtags.forEach { hashtag ->
            val indexOf = text.indexOf(hashtag.hashtag)
            text.replace(
                indexOf,
                indexOf + hashtag.hashtag.length,
                shellHelper.getColored(hashtag.hashtag, PromptColor.BLUE).toAnsi()
            )
        }

        return AttributedStringBuilder()
            .append(shellHelper.getColored(TemporalUtils.toSystemZone(tweet.createdDate)
                                               .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                                           PromptColor.MAGENTA))
            .append(" - ")
            .append(shellHelper.getColored(tweet.user.username, PromptColor.RED))
            .append(": ")
            .append(text)
            .append(" (").append(shellHelper.getColored(tweet.uid, PromptColor.YELLOW).toAnsi()).append(")")
            .toAttributedString()
    }
}