package com.github.twitterclone.server.controller

import com.github.twitterclone.sdk.domain.tweet.NewTweet
import com.github.twitterclone.sdk.domain.tweet.Tweet
import com.github.twitterclone.sdk.domain.tweet.TweetQuery
import com.github.twitterclone.server.config.Log
import com.github.twitterclone.server.mapper.TweetMapper
import com.github.twitterclone.server.model.document.user.User
import com.github.twitterclone.server.service.TweetService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Controller
class TweetController(
    private val tweetService: TweetService,
    private val tweetMapper: TweetMapper,
) {
    companion object : Log()

    private val requesterMap: ConcurrentMap<String, MutableList<RSocketRequester>> = ConcurrentHashMap()

    @ConnectMapping("tweets.{username}")
    fun connect(
        @AuthenticationPrincipal principal: User,
        @DestinationVariable username: String,
        rSocketRequester: RSocketRequester,
    ) {
        GlobalScope.launch {
            rSocketRequester.rsocket()!!
                .onClose()
                .subscribe(null, null) {
                    val requesterList = requesterMap[username]
                    requesterList?.remove(rSocketRequester)
                }
        }
        requesterMap[username] = (requesterMap[username] ?: mutableListOf()).apply {
            add(rSocketRequester)
        }
    }


    @MessageMapping("tweet")
    fun newTweet(@AuthenticationPrincipal principal: User, tweetDto: NewTweet): Mono<Tweet> {
        return tweetService.newTweet(tweetMapper.newTweetDtoToTweet(tweetDto))
            .map(tweetMapper::tweetToDto)
            .doOnNext { createdTweetDto ->
                requesterMap[createdTweetDto.user?.username]?.forEach { rSocketRequester ->
                    rSocketRequester.route("tweet")
                        .data(createdTweetDto)
                        .send()
                        .subscribe()
                }
            }
    }

    @MessageMapping("tweets")
    fun findTweets(
        @AuthenticationPrincipal principal: User,
        @Validated queryDto: TweetQuery,
    ): Flux<Tweet> {
        return tweetService.find(queryDto)
            .map(tweetMapper::tweetToDto)
    }

}