package com.example.twitterclone.controller

import com.example.twitterclone.config.Log
import com.example.twitterclone.mapper.TweetMapper
import com.example.twitterclone.model.document.user.User
import com.example.twitterclone.model.dto.tweet.TweetDto
import com.example.twitterclone.model.dto.tweet.TweetQueryDto
import com.example.twitterclone.service.TweetService
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
    companion object : Log();

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
    fun newTweet(@AuthenticationPrincipal principal: User, tweetDto: TweetDto): Mono<TweetDto> {
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
            @Validated queryDto: TweetQueryDto,
    ): Flux<TweetDto> {
        return tweetService.find(queryDto)
                .map(tweetMapper::tweetToDto)
    }

}