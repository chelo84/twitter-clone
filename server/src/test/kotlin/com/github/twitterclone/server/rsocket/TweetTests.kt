package com.github.twitterclone.server.rsocket

import com.github.twitterclone.sdk.domain.tweet.Tweet
import com.github.twitterclone.sdk.domain.tweet.TweetQuery
import com.github.twitterclone.server.config.Log
import com.github.twitterclone.server.repository.tweet.TweetRepository
import io.rsocket.metadata.WellKnownMimeType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.test.annotation.DirtiesContext
import org.springframework.util.MimeTypeUtils
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TweetTests : TwitterCloneTests() {
    companion object : Log() {
        fun fakeTweetDto(): Tweet {
            return Tweet().apply {
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
            .metadata(
                BearerTokenMetadata(fakeAuthentication.token),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(tweetDto)
            .retrieveMono(Tweet::class.java)
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
            .metadata(
                BearerTokenMetadata(fakeAuthentication.token),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(TweetQuery(fakeTweet.user!!.username!!, 0, 20))
            .retrieveFlux(Tweet::class.java)

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
            .metadata(
                BearerTokenMetadata(fakeAuthentication.token),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(TweetQuery(firstTweet.user!!.username!!, 0, 1))
            .retrieveFlux(Tweet::class.java)

        val pageTwo = createRSocketRequester()
            .route("tweets")
            .metadata(
                BearerTokenMetadata(fakeAuthentication.token),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(TweetQuery(firstTweet.user!!.username!!, 1, 1))
            .retrieveFlux(Tweet::class.java)

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
            .metadata(
                BearerTokenMetadata(fakeAuthentication.token),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(TweetQuery("test", -10, 1))
            .retrieveFlux(Tweet::class.java)

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
            .metadata(
                BearerTokenMetadata(fakeAuthentication.token),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .sendMetadata()
            .block()

        val fakeTweetDto = fakeTweetDto()

        // when
        val createdTweet = createRSocketRequester()
            .route("tweet")
            .metadata(
                BearerTokenMetadata(otherUserToken),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(fakeTweetDto)
            .retrieveMono(Tweet::class.java)
            .block()

        // then
        Assertions.assertNotNull(tweetHandler)
        Assertions.assertNotNull(tweetHandler.newTweet)
        Assertions.assertEquals(tweetHandler.newTweet!!.uid, createdTweet!!.uid)
    }

    fun createFakeTweet(): Tweet {
        return createRSocketRequester()
            .route("tweet")
            .metadata(
                BearerTokenMetadata(fakeAuthentication.token),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(fakeTweetDto())
            .retrieveMono(Tweet::class.java)
            .block()!!
    }
}

private class TweetHandler {
    companion object : Log()

    var newTweet: Tweet? = null

    @MessageMapping("tweet")
    fun newTweet(payload: Tweet): Mono<Void> {
        newTweet = payload
        return Mono.empty()
    }
}
