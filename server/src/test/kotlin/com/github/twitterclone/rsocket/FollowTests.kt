package com.github.twitterclone.rsocket

import com.github.twitterclone.config.Log
import com.github.twitterclone.controller.FollowController.Companion.FOLLOW
import com.github.twitterclone.controller.FollowController.Companion.UNFOLLOW
import com.github.twitterclone.model.dto.follow.FollowDto
import com.github.twitterclone.model.dto.user.UserDto
import com.github.twitterclone.repository.follow.FollowRepository
import com.github.twitterclone.security.jwt.JWTTokenService
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
class FollowTests : TwitterCloneTests() {
    companion object : Log()

    @Autowired
    private lateinit var followRepository: FollowRepository

    @Test
    fun `Should follow an user`() {
        // given
        val userToFollow = newFakeUserAndToken().first

        // when
        val follow = simpleFollowSocketRequester(userToFollow.username)
                .block()

        // then
        Assertions.assertNotNull(follow)
        Assertions.assertNotNull(follow?.followDate)
        Assertions.assertNotNull(follow?.id)
        Assertions.assertEquals(fakeAuthentication.principal.username, follow?.pair?.follower)
        Assertions.assertEquals(userToFollow.username, follow?.pair?.followed)
    }

    @Test
    fun `User should receive a notification when someone follows them`() {
        // given
        val userOne = fakeAuthentication.principal
        val userOneToken = fakeAuthentication.token
        val (userTwo, userTwoToken) = newFakeUserAndToken()

        val followHandler = FollowHandler()
        val rSocketRequester = createRSocketRequester(followHandler)

        // when
        log.info("setup connection to follow messageMapping for userOne")
        rSocketRequester
                .route(FOLLOW)
                .metadata(userOneToken,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .sendMetadata()
                .block()

        log.info("userTwo follows userOne")
        rSocketRequester
                .route(FOLLOW)
                .metadata(userTwoToken,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(userOne.username)
                .retrieveMono(FollowDto::class.java)
                .block()

        // then
        log.info("userOne should be notificated about userTwo following them")
        Assertions.assertNotNull(followHandler)
        Assertions.assertNotNull(followHandler.followedBy)
        Assertions.assertEquals(userTwo.username, followHandler.followedBy!!.username)
    }

    @Test
    fun `Should throw error when trying to follow itself`() {
        // given
        val followSocketRequester = simpleFollowSocketRequester(fakeAuthentication.principal.username)

        // then
        StepVerifier.create(followSocketRequester)
                .verifyErrorMatches { it.message == "User cannot follow itself" }
    }

    @Test
    fun `Should throw error if the followed user does not exist`() {
        // given
        val nonExistentId = "nonExistentId"

        // when
        val followSocketRequester = simpleFollowSocketRequester(nonExistentId)

        // then
        StepVerifier.create(followSocketRequester)
                .verifyErrorMatches { it.message == "User with username $nonExistentId not found" }
    }

    @Test
    fun `Should throw error when trying to follow an user already followed`() {
        // given
        val userToFollow = newFakeUserAndToken().first
        simpleFollowSocketRequester(userToFollow.username)
                .block()

        // when
        val followSocketRequester = simpleFollowSocketRequester(userToFollow.username)

        // then
        StepVerifier.create(followSocketRequester)
                .verifyErrorMatches { it.message == "User already followed" }
    }

    private fun simpleFollowSocketRequester(data: String): Mono<FollowDto> {
        return createRSocketRequester()
                .route(FOLLOW)
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(data)
                .retrieveMono(FollowDto::class.java)
    }

    @Test
    fun `Should unfollow an user`() {
        // given
        val userToUnfollow = newFakeUserAndToken().first
        simpleFollowSocketRequester(userToUnfollow.username)
                .block()
        Assertions.assertNotNull(followRepository.findByPair_FollowerAndPair_Followed(fakeAuthentication.principal.username,
                                                                                      userToUnfollow.username).block())

        // when
        createRSocketRequester()
                .route(UNFOLLOW)
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(userToUnfollow.username)
                .retrieveMono(Void::class.java)
                .block()

        // then
        Assertions.assertNull(followRepository.findByPair_FollowerAndPair_Followed(fakeAuthentication.principal.username,
                                                                                   userToUnfollow.username).block())
    }

    @Test
    fun `User should receive a notification when someone unfollows them`() {
        // given
        val userOne = fakeAuthentication.principal
        val userOneToken = fakeAuthentication.token
        val userTwo = newFakeUserAndToken().first
        val userTwoToken = JWTTokenService.generateToken(userTwo.username, listOf())

        val followHandler = FollowHandler()
        val rSocketRequester = createRSocketRequester(followHandler)
        rSocketRequester
                .route(FOLLOW)
                .metadata(userOneToken,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(userTwo.username)
                .retrieveMono(Void::class.java)
                .block()

        // when
        log.info("setup connection to follow messageMapping for userFollowed")
        rSocketRequester
                .route(FOLLOW)
                .metadata(userTwoToken,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .sendMetadata()
                .block()

        log.info("userFollower unfollows userFollowed")
        rSocketRequester
                .route(UNFOLLOW)
                .metadata(userOneToken,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(userTwo.username)
                .retrieveMono(Void::class.java)
                .block()

        // then
        log.info("userTwo should be notificated about userOne unfollowing them")
        Assertions.assertNotNull(followHandler)
        Assertions.assertNotNull(followHandler.unfollowedBy)
        Assertions.assertEquals(userOne.username, followHandler.unfollowedBy!!.username)
    }

    @Test
    fun `Should throw error if the unfollowed user does not exist`() {
        // given
        val nonExistentUsername = "nonExistentUser"

        // when
        val unfollowSocketRequester = createRSocketRequester()
                .route(UNFOLLOW)
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(nonExistentUsername)
                .retrieveMono(Void::class.java)

        // then
        StepVerifier.create(unfollowSocketRequester)
                .verifyErrorMatches { it.message == "User with username $nonExistentUsername not found" }
    }

    @Test
    fun `Should throw error if the user tries to follow someone that they don't follow`() {
        // given
        val anotherUser = newFakeUserAndToken().first

        // when
        val unfollowSocketRequester = createRSocketRequester()
                .route(UNFOLLOW)
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(anotherUser.username)
                .retrieveMono(Void::class.java)

        // then
        StepVerifier.create(unfollowSocketRequester)
                .verifyErrorMatches { it.message == "User need to be followed in order to unfollow them" }
    }
}

class FollowHandler {
    companion object : Log()

    var followedBy: UserDto? = null
    var unfollowedBy: UserDto? = null

    @MessageMapping(FOLLOW)
    fun follow(payload: UserDto): Mono<Void> {
        followedBy = payload
        return Mono.empty()
    }

    @MessageMapping(UNFOLLOW)
    fun unfollow(payload: UserDto): Mono<Void> {
        unfollowedBy = payload
        return Mono.empty()
    }
}