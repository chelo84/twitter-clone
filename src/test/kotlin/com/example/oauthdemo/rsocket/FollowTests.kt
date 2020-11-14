package com.example.oauthdemo.rsocket

import com.example.oauthdemo.config.Log
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FollowTests : ZakoomTests() {
    companion object : Log()

    @Autowired
    private val builder: RSocketRequester.Builder? = null


    private fun createRSocketRequester(): RSocketRequester? {
        return builder!!.tcp("localhost", 7000)
    }

    @Test
    fun test() {
        val userFollowed = newFakeUser()
        val string = createRSocketRequester()!!
                .route("follow")
                .metadata(fakePrincipal.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(userFollowed.id!!)
                .retrieveMono(String::class.java)
                .doOnError(Exception::class.java) { e -> log.error(e.message) }
                .block()


        // then
//        StepVerifier.create(prices.take(5))
//                .expectNextMatches(Predicate<T> { stockPrice: T -> stockPrice.getSymbol().equals("SYMBOL") })
//                .expectNextMatches(Predicate<T> { stockPrice: T -> stockPrice.getSymbol().equals("SYMBOL") })
//                .expectNextMatches(Predicate<T> { stockPrice: T -> stockPrice.getSymbol().equals("SYMBOL") })
//                .expectNextMatches(Predicate<T> { stockPrice: T -> stockPrice.getSymbol().equals("SYMBOL") })
//                .expectNextMatches(Predicate<T> { stockPrice: T -> stockPrice.getSymbol().equals("SYMBOL") })
//                .verifyComplete()
    }
}