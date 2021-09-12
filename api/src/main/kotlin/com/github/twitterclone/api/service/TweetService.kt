package com.github.twitterclone.api.service

import com.github.twitterclone.api.model.document.Tweet
import com.github.twitterclone.api.model.document.user.User
import com.github.twitterclone.api.repository.tweet.TweetRepository
import com.github.twitterclone.sdk.domain.tweet.TweetQuery
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime


@Service
class TweetService(
    private val tweetRepository: TweetRepository,
    private val hashtagService: HashtagService,
) {
    fun newTweet(principal: User, tweet: Tweet): Mono<Tweet> {
        return hashtagService.getHashtags(tweet.text)
            .collectList()
            .flatMap { hashtags ->
                val replyList: Mono<List<Tweet>> = tweet.replyTo?.let {
                    tweetRepository.findAllByUidLike(tweet.replyTo!!).collectList()
                } ?: Mono.just(listOf())
                replyList.flatMap { tweets ->
                    if (tweets.size > 1) {
                        throw Exception("Found multiple tweets with ID ${tweet.replyTo}, please be more specific!")
                    }

                    val replyTo = tweets.getOrNull(0)
                    tweetRepository.save(
                        tweet.apply {
                            this.createdDate = LocalDateTime.now()
                            this.user = principal.username
                            this.hashtags = hashtags
                            this.replyTo = replyTo?.uid
                        }
                    )
                }
            }
    }

    fun find(queryDto: TweetQuery): Flux<Tweet> {
        return tweetRepository.findAllByUserAndReplyToIsNullOrderByCreatedDateDesc(queryDto.username,
                                                                                   PageRequest.of(queryDto.page,
                                                                                                  queryDto.size))
    }
}