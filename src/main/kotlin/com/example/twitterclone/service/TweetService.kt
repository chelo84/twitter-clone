package com.example.twitterclone.service

import com.example.twitterclone.exception.NotFoundException
import com.example.twitterclone.model.document.Tweet
import com.example.twitterclone.repository.tweet.TweetRepository
import org.springframework.stereotype.Service
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
                            tweet.apply { this.hashtags = hashtags }
                    )
                }
    }

    fun find(id: String): Mono<Tweet> {
        return tweetRepository.findById(id)
                .switchIfEmpty(Mono.error(NotFoundException("No post was found with id $id")))
    }
}