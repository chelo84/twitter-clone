package com.example.twitterclone.rsocket

import com.example.twitterclone.config.Log
import com.example.twitterclone.controller.FollowController.Companion.FOLLOW
import com.example.twitterclone.controller.FollowController.Companion.UNFOLLOW
import com.example.twitterclone.model.dto.follow.FollowDto
import com.example.twitterclone.model.dto.user.UserDto
import com.example.twitterclone.repository.follow.FollowRepository
import com.example.twitterclone.security.jwt.JWTTokenService
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
        val userOne = fakeAuthentication.principal
        val userOneToken = fakeAuthentication.token
        val userTwo = newFakeUser()
        val userTwoToken = JWTTokenService.generateToken(userTwo.username, userTwo, listOf())

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
                .data(userOne.id!!)
                .retrieveMono(FollowDto::class.java)
                .block()

        // then
        log.info("userOne should be notificated about userTwo following them")
        Assertions.assertNotNull(followHandler)
        Assertions.assertNotNull(followHandler.followedBy)
        Assertions.assertEquals(userTwo.id, followHandler.followedBy!!.id)
    }

    @Test
    fun `Should throw error when trying to follow itself`() {
        // given
        val followSocketRequester = simpleFollowSocketRequester(fakeAuthentication.principal.id!!)

        // then
        StepVerifier.create(followSocketRequester)
                .verifyErrorSatisfies { it.message == "User cannot follow itself" }
    }

    @Test
    fun `Should throw error if the followed user does not exist`() {
        // given
        val nonExistentId = "nonExistentId"

        // when
        val followSocketRequester = simpleFollowSocketRequester(nonExistentId)

        // then
        StepVerifier.create(followSocketRequester)
                .verifyErrorSatisfies { it.message == "User with ID $nonExistentId not found" }
    }

    @Test
    fun `Should throw error when trying to follow an user already followed`() {
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
        val userToUnfollow = newFakeUser()
        simpleFollowSocketRequester(userToUnfollow.id!!)
                .block()
        Assertions.assertNotNull(followRepository.findByPair_FollowerAndPair_Followed(fakeAuthentication.principal.id!!,
                                                                                      userToUnfollow.id!!).block())

        // when
        createRSocketRequester()
                .route(UNFOLLOW)
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(userToUnfollow.id!!)
                .retrieveMono(Void::class.java)
                .block()

        // then
        Assertions.assertNull(followRepository.findByPair_FollowerAndPair_Followed(fakeAuthentication.principal.id!!,
                                                                                   userToUnfollow.id!!).block())
    }

    @Test
    fun `User should receive a notification when someone unfollows them`() {
        // given
        val userOne = fakeAuthentication.principal
        val userOneToken = fakeAuthentication.token
        val userTwo = newFakeUser()
        val userTwoToken = JWTTokenService.generateToken(userTwo.username, userTwo, listOf())

        val followHandler = FollowHandler()
        val rSocketRequester = createRSocketRequester(followHandler)
        rSocketRequester
                .route(FOLLOW)
                .metadata(userOneToken,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(userTwo.id!!)
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
                .data(userTwo.id!!)
                .retrieveMono(Void::class.java)
                .block()

        // then
        log.info("userTwo should be notificated about userOne unfollowing them")
        Assertions.assertNotNull(followHandler)
        Assertions.assertNotNull(followHandler.unfollowedBy)
        Assertions.assertEquals(userOne.id, followHandler.unfollowedBy!!.id)
    }

    @Test
    fun `Should throw error if the unfollowed user does not exist`() {
        // given
        val nonExistentUserId = "nonExistentUser"

        // when
        val unfollowSocketRequester = createRSocketRequester()
                .route(UNFOLLOW)
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(nonExistentUserId)
                .retrieveMono(Void::class.java)

        // then
        StepVerifier.create(unfollowSocketRequester)
                .verifyErrorMatches { it.message == "User with ID $nonExistentUserId not found" }
    }

    @Test
    fun `Should throw error if the user tries to follow someone that they don't follow`() {
        // given
        val anotherUser = newFakeUser()

        // when
        val unfollowSocketRequester = createRSocketRequester()
                .route(UNFOLLOW)
                .metadata(fakeAuthentication.token,
                          BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .data(anotherUser.id!!)
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