package com.github.twitterclone.service

import com.github.twitterclone.model.document.Tweet
import com.github.twitterclone.model.dto.tweet.TweetQueryDto
import com.github.twitterclone.repository.tweet.TweetRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class TweetService(
        private val tweetRepository: TweetRepository,
        private val hashtagService: HashtagService,
) {
    fun newTweet(tweet: Tweet): Mono<Tweet> {
        return hashtagService.getHashtags(tweet.text)
                .collectList()
                .flatMap { hashtags ->
                    tweetRepository.save(
                            tweet.apply { this.hashtags = hashtags.map { it.hashtag } }
                    )
                }
    }

    fun find(queryDto: TweetQueryDto): Flux<Tweet> {
        return tweetRepository.findAllByUser(queryDto.username, PageRequest.of(queryDto.page, queryDto.size))
    }
}