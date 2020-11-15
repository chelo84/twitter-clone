package com.example.twitterclone.rsocket

import com.example.twitterclone.config.Log
import com.example.twitterclone.model.document.follow.Follow
import com.example.twitterclone.model.dto.user.UserDto
import com.example.twitterclone.security.jwt.JWTTokenService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.test.annotation.DirtiesContext
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FollowTests : TwitterCloneTests() {
    companion object : Log()

    @Test
    fun `Should follow an user`() {
        // given
        val userToFollow = newFakeUser()

        // when
        val follow = simpleFollowSocketRequester(userToFollow.id!!)
                .block()

        // then
        Assertions.assertNotNull(follow)
        Assertions.assertNotNull(follow?.followDate)
        Assertions.assertNotNull(follow?.id)
        Assertions.assertEquals(fakeAuthentication.principal.id, follow?.pair?.follower)
        Assertions.assertEquals(userToFollow.id, follow?.pair?.followed)
    }

    @Test
    fun `User should receive a notification when someone follows them`() {
        // given
        val userFollowed = fakeAuthentication.principal
        val userFollowedToken = fakeAuthentication.token
        val userFollower = newFakeUser()
        val userFollowerToken = JWTTokenService.generateToken(userFollower.username, userFollower, listOf())

        val followHandler = FollowHandler()
        val rSocketRequester = createRSocketRequester(followHandler)

        // when
        log.info("setup connection to follow messageMapping for userFollowed")
        rSocketRequester
                .route("follow")
                .metadata(userFollowedToken,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .sendMetadata()
                .block()

        log.info("userFollower follows userFollowed")
        rSocketRequester
                .route("follow")
                .metadata(userFollowerToken,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(userFollowed.id!!)
                .retrieveMono(Follow::class.java)
                .block()

        // then
        log.info("userFollowed should be notificated about userFollower following them")
        Assertions.assertNotNull(followHandler)
        Assertions.assertNotNull(followHandler.followedBy)
        Assertions.assertEquals(userFollower.id, followHandler.followedBy!!.id)
    }

    @Test
    fun `Should throw an error when trying to follow itself`() {
        // given
        val followSocketRequester = simpleFollowSocketRequester(fakeAuthentication.principal.id!!)

        // then
        StepVerifier.create(followSocketRequester)
                .verifyErrorSatisfies { it.message == "User cannot follow itself" }
    }

    @Test
    fun `Should throw an error if the user tries to follow a non existent user`() {
        // given
        val nonExistentId = "nonExistentId"

        // when
        val followSocketRequester = simpleFollowSocketRequester(nonExistentId)

        // then
        StepVerifier.create(followSocketRequester)
                .verifyErrorSatisfies { it.message == "User with ID $nonExistentId not found" }
    }

    @Test
    fun `Should throw an error when trying to follow an user already followed`() {
        // given
        val userToFollow = newFakeUser()
        simpleFollowSocketRequester(userToFollow.id!!)
                .block()

        // when
        val followSocketRequester = simpleFollowSocketRequester(userToFollow.id!!)

        // then
        StepVerifier.create(followSocketRequester)
                .verifyErrorMatches { it.message == "User already followed" }
    }

    private fun simpleFollowSocketRequester(data: String): Mono<Follow> {
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

class FollowHandler {
    companion object : Log()

    var followedBy: UserDto? = null

    @MessageMapping("follow")
    fun follow(payload: UserDto): Mono<Void> {
        followedBy = payload
        return Mono.empty()
    }
}