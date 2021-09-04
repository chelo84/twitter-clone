package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.command.TweetCommand
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.tweet.Tweet
import org.springframework.messaging.handler.annotation.MessageMapping
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

class TweetHandler(private val shellHelper: ShellHelper, properties: TweetProperties) :
    Handler<TweetProperties>(shellHelper, properties) {

    private val tweets: Sinks.Many<Tweet> = Sinks.many().multicast().directBestEffort()
    fun getTweets(): Flux<Tweet> = tweets.asFlux()

    /**
     * Called whenever the [TweetProperties.username] creates a new tweet
     *
     * Adds the new tweet to the [tweets] sink
     * @param tweet: the newly created [Tweet]
     * @see [TweetCommand.tweets]
     */
    @MessageMapping("tweet")
    fun newTweet(tweet: Tweet) {
        tweets.tryEmitNext(tweet)
    }

    override fun dispose() {
        shellHelper.printInfo("DISPOSE TWEETS")

        tweets.tryEmitComplete()
    }
}

class TweetProperties(
    val username: String,
) : HandlerProperties