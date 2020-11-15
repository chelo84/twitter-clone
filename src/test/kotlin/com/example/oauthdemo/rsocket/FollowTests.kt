package com.example.oauthdemo.rsocket

import com.example.oauthdemo.config.Log
import com.example.oauthdemo.model.document.follow.Follow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.test.annotation.DirtiesContext
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FollowTests : ZakoomTests() {
    companion object : Log()

    @Test
    fun `Should follow an user`() {
        // given
        val userToFollow = newFakeUser()

        // when
        val follow = followSocketRequester(userToFollow.id!!)
                .block()

        // then
        Assertions.assertNotNull(follow)
        Assertions.assertNotNull(follow?.followDate)
        Assertions.assertNotNull(follow?.id)
        Assertions.assertEquals(fakeAuthentication.principal.id, follow?.pair?.follower)
        Assertions.assertEquals(userToFollow.id, follow?.pair?.followed)
    }

    @Test
    fun `Should throw an error when trying to follow itself`() {
        // given
        val followSocketRequester = followSocketRequester(fakeAuthentication.principal.id!!)

        // then
        StepVerifier.create(followSocketRequester)
                .verifyErrorSatisfies { it.message == "User cannot follow itself" }
    }

    @Test
    fun `Should throw an error if the user tries to follow a non existent user`() {
        // given
        val nonExistentId = "nonExistentId"

        // when
        val followSocketRequester = followSocketRequester(nonExistentId)

        // then
        StepVerifier.create(followSocketRequester)
                .verifyErrorSatisfies { it.message == "User with ID $nonExistentId not found" }
    }

    @Test
    fun `Should throw an error when trying to follow an user already followed`() {
        // given
        val userToFollow = newFakeUser()
        followSocketRequester(userToFollow.id!!)
                .block()

        // when
        val followSocketRequester = followSocketRequester(userToFollow.id!!)

        // then
        StepVerifier.create(followSocketRequester)
                .verifyErrorMatches { it.message == "User already followed" }
    }

    private fun followSocketRequester(data: String): Mono<Follow> {
        return createRSocketRequester()
                .route("follow")
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(data)
                .retrieveMono(Follow::class.java)
    }

    @Test
    fun `Should unfollow an user`() {
        TODO()
    }
}