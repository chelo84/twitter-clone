package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.tweet.Tweet
import org.springframework.messaging.handler.annotation.MessageMapping
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

class TweetsHandler(private val shellHelper: ShellHelper, args: Map<out HandlerArgument, Any>) :
    Handler(shellHelper, args) {

    val username: String
    private val tweets: Sinks.Many<Tweet> = Sinks.many().multicast().directBestEffort()
    fun getTweets(): Flux<Tweet> = tweets.asFlux()

    enum class TweetsArgument(override val value: String) : HandlerArgument {
        USERNAME("username")
    }

    init {
        this.username = args[TweetsArgument.USERNAME] as String? ?: throw Exception("username is required")
    }

    /**
     * Called whenever the user with [username] creates a new tweet
     * Adds the new tweet to the [tweets] sink
     * @param tweet: the newly created [Tweet]
     */
    @MessageMapping("tweet")
    fun follow(tweet: Tweet) {
        tweets.tryEmitNext(tweet)
    }

    override fun dispose() {
        shellHelper.printInfo("DISPOSE TWEETS")

        tweets.tryEmitComplete()
    }
}