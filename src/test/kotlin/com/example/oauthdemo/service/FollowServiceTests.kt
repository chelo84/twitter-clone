package com.example.oauthdemo.service

import com.example.oauthdemo.mapper.UserMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
@ExtendWith(MockitoExtension::class)
class FollowServiceTests : ZakoomTests() {
    companion object {

    }

    @Autowired
    private lateinit var followService: FollowService

    @Autowired
    private lateinit var signupService: SignupService

    @Autowired
    private lateinit var userMapper: UserMapper

    @Test
    fun `User should follow another user`() {
        // given
        var userOne = userMapper.dtoToUser(SignupServiceTests.createFakeUserDto())
        userOne = signupService.signup(userOne).block()!!
        var userTwo = userMapper.dtoToUser(SignupServiceTests.createFakeUserDto())
        userTwo = signupService.signup(userTwo).block()!!

        // when
        val userOneFollowsUserTwo = followService.follow(userOne, userTwo.id!!).block()

        // then
        Assertions.assertNotNull(userOneFollowsUserTwo)
        userOneFollowsUserTwo!!
        Assertions.assertNotNull(userOneFollowsUserTwo.id)
    }

    fun `Should throw error if the user tries to follow an user that it already follows`() {
        TODO()
    }

    fun `Should throw error if the pair is null`() {
        TODO()
    }

    fun `Should throw error if the pair has elements missing`() {
        TODO()
    }

    fun `User should unfollow another user`() {
        TODO()
    }

    fun `Should throw error if user tries to unfollow someone that it doesn't follow`() {
        TODO()
    }
}