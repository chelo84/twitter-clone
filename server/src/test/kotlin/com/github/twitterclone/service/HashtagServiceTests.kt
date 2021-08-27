package com.github.twitterclone.service

import com.github.twitterclone.rsocket.TwitterCloneTests
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import java.time.Duration

@SpringBootTest
@ExtendWith(MockitoExtension::class)
class HashtagServiceTests : TwitterCloneTests() {

    @Autowired
    private lateinit var hashtagService: HashtagService

    @Test
    fun `Should create all hashtags in text`() {
        // given
        val text = "This text has two #hashtags because i #want"
        val textHashtags = mutableListOf("#hashtags", "#want")

        // then
        StepVerifier.create(hashtagService.getHashtags(text))
                .expectNextMatches { textHashtags.remove(it.hashtag) }
                .expectNextMatches { textHashtags.remove(it.hashtag) }
                .verifyComplete()
        Assertions.assertEquals(0, textHashtags.size)
    }

    @Test
    fun `Should ignore words that have more than one sharp (#) in its name`() {
        // given
        val text = "This #hashtag#withMoreThanOneSharp shouldn't be considered #test#1#2#3 #trueHashtag"
        val textHashtags = mutableListOf("#trueHashtag")

        // then
        StepVerifier.create(hashtagService.getHashtags(text))
                .expectNextMatches { textHashtags.remove(it.hashtag) }
                .verifyComplete()
        Assertions.assertEquals(0, textHashtags.size)
    }

    @Test
    fun `Should return empty Flux when there is not hashtag in text`() {
        // given
        val text = "This text does not contain any hashtag"

        // then
        StepVerifier.create(hashtagService.getHashtags(text))
                .expectSubscription()
                .verifyComplete()
    }

    @Test
    fun `Should return an already created hashtag if one exists`() {
        // given
        val firstTweet = "Tweet with one #hashtag"
        val secondTweet = "Tweet with the previous #hashtag"
        val hashtags = mutableListOf("#hashtag")

        // when
        val createdHashtag = hashtagService.getHashtags(firstTweet)
                .blockLast(Duration.ofSeconds(5))!!

        // then
        StepVerifier.create(hashtagService.getHashtags(secondTweet))
                .expectNextMatches {
                    it.hashtag == createdHashtag.hashtag &&
                            hashtags.remove(it.hashtag)
                }
                .verifyComplete()
        Assertions.assertEquals(0, hashtags.size)
    }
}