package com.github.twitterclone.rsocket

import com.github.twitterclone.config.Log
import com.github.twitterclone.model.dto.tweet.TweetDto
import com.github.twitterclone.model.dto.tweet.TweetQueryDto
import com.github.twitterclone.repository.tweet.TweetRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.test.annotation.DirtiesContext
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TweetTests : TwitterCloneTests() {
    companion object : Log() {
        fun fakeTweetDto(): TweetDto {
            return TweetDto().apply {
                this.text = podamFactory.manufacturePojo(String::class.java)
            }
        }
    }

    @Autowired
    private lateinit var tweetRepository: TweetRepository

    @Test
    fun `Should create a new tweet`() {
        // given
        val tweetDto = fakeTweetDto()
        tweetDto.text = "tweet with 2 #hashtags #second :)"
        val hashtags = listOf("#hashtags", "#second")

        // when
        val newTweet = createRSocketRequester()
                .route("tweet")
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(tweetDto)
                .retrieveMono(TweetDto::class.java)
                .block()

        // then
        Assertions.assertNotNull(newTweet)
        newTweet!!
        Assertions.assertNotNull(newTweet.createdDate)
        Assertions.assertNotNull(newTweet.user)
        Assertions.assertNotNull(newTweet.hashtags)
        Assertions.assertTrue { newTweet.hashtags.size == 2 }
        Assertions.assertTrue {
            hashtags.contains(newTweet.hashtags[0]) &&
                    hashtags.contains(newTweet.hashtags[1])
        }
    }

    @Test
    fun `Should search tweets by user`() {
        // given
        val fakeTweet = createFakeTweet()

        // when
        val tweetsFlux = createRSocketRequester()
                .route("tweets")
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(TweetQueryDto(fakeTweet.user!!.username!!, 0, 20))
                .retrieveFlux(TweetDto::class.java)

        // then
        StepVerifier.create(tweetsFlux)
                .expectNextMatches { it.text == fakeTweet.text }
                .verifyComplete()
    }

    @Test
    fun `Should search two pages of tweets by user`() {
        // given
        val firstTweet = createFakeTweet()
        val secondTweet = createFakeTweet()

        // when
        val pageOne = createRSocketRequester()
                .route("tweets")
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(TweetQueryDto(firstTweet.user!!.username!!, 0, 1))
                .retrieveFlux(TweetDto::class.java)

        val pageTwo = createRSocketRequester()
                .route("tweets")
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(TweetQueryDto(firstTweet.user!!.username!!, 1, 1))
                .retrieveFlux(TweetDto::class.java)

        // then
        StepVerifier.create(pageOne)
                .expectNextMatches { it.text == firstTweet.text }
                .verifyComplete()
        StepVerifier.create(pageTwo)
                .expectNextMatches { it.text == secondTweet.text }
                .verifyComplete()
    }

    @Test
    fun `Should throw error if the dto is not valid`() {
        // given
        val search = createRSocketRequester()
                .route("tweets")
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(TweetQueryDto("test", -10, 1))
                .retrieveFlux(TweetDto::class.java)

        // then
        StepVerifier.create(search)
                .expectErrorMatches { it.message?.contains("field 'page': rejected value [-10]") ?: false }
                .verify()
    }

    @Test
    fun `Should make a Connect request`() {
        // given
        val (otherUser, otherUserToken) = newFakeUserAndToken()

        val tweetHandler = TweetHandler()
        val rSocketRequester = createRSocketRequester(tweetHandler)
        rSocketRequester
                .route("tweets.${otherUser.username}")
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .sendMetadata()
                .block()

        val fakeTweetDto = fakeTweetDto()

        // when
        val createdTweet = createRSocketRequester()
                .route("tweet")
                .metadata(otherUserToken,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(fakeTweetDto)
                .retrieveMono(TweetDto::class.java)
                .block()

        // then
        Assertions.assertNotNull(tweetHandler)
        Assertions.assertNotNull(tweetHandler.newTweet)
        Assertions.assertEquals(tweetHandler.newTweet!!.uid, createdTweet!!.uid)
    }

    fun createFakeTweet(): TweetDto {
        return createRSocketRequester()
                .route("tweet")
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(fakeTweetDto())
                .retrieveMono(TweetDto::class.java)
                .block()!!
    }
}

private class TweetHandler {
    companion object : Log()

    var newTweet: TweetDto? = null

    @MessageMapping("tweet")
    fun newTweet(payload: TweetDto): Mono<Void> {
        newTweet = payload
        return Mono.empty()
    }
}
