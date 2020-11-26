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

    private val requesterMap: ConcurrentMap<String, RSocketRequester> = ConcurrentHashMap()

    @ConnectMapping("tweet.{username}")
    fun connect(
            @AuthenticationPrincipal principal: User,
            @DestinationVariable username: String,
            rSocketRequester: RSocketRequester,
    ) {
        GlobalScope.launch {
            rSocketRequester.rsocket()!!
                    .onClose()
                    .subscribe(null, null) { requesterMap.remove(principal.id!!, rSocketRequester) }
        }

        requesterMap[principal.id!!] = rSocketRequester
    }


    @MessageMapping("tweet")
    fun newTweet(@AuthenticationPrincipal principal: User, tweetDto: TweetDto): Mono<TweetDto> {
        return tweetService.newTweet(tweetMapper.newTweetDtoToTweet(tweetDto))
                .map(tweetMapper::tweetToDto)
//                .doOnNext {
//                    requesterMap[id]?.route(FollowController.FOLLOW)
//                            ?.data(userMapper.findAndMapUserToUserDto(it.pair.follower))
//                            ?.send()
//                            ?.subscribe()
//                }
    }

    @MessageMapping("tweets")
    fun findTweets(
            @AuthenticationPrincipal principal: User,
            queryDto: TweetQueryDto,
    ): Flux<TweetDto> {
        queryDto.validateQuery()
        return tweetService.find(queryDto)
                .map(tweetMapper::tweetToDto)
    }

}