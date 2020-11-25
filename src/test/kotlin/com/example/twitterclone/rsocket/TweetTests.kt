package com.example.twitterclone.rsocket

import com.example.twitterclone.config.Log
import com.example.twitterclone.model.dto.TweetDto
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.test.annotation.DirtiesContext

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
        Assertions.assertTrue { newTweet.hashtags!!.size == 2 }
        Assertions.assertTrue {
            hashtags.contains(newTweet.hashtags!![0].hashtag) &&
                    hashtags.contains(newTweet.hashtags!![1].hashtag)
        }
    }

    @Test
    fun `Should search tweets by user`() {
        TODO()
    }

    @Test
    fun `Should search tweets in batches (offset to limit)`() {
        TODO()
    }
}