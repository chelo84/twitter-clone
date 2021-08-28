package com.github.twitterclone.server.service

import com.github.twitterclone.sdk.domain.user.User
import com.github.twitterclone.server.exception.UserAlreadyExistsException
import com.github.twitterclone.server.mapper.UserMapper
import com.github.twitterclone.server.rsocket.TwitterCloneTests
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.test.StepVerifier

@SpringBootTest
@ExtendWith(MockitoExtension::class)
class SignupServiceTests : TwitterCloneTests() {
    companion object {
        fun createFakeUserDto(): User {
            val fakeUserDto = User()
            fakeUserDto.name = podamFactory.manufacturePojo(String::class.java)
            fakeUserDto.username = podamFactory.manufacturePojo(String::class.java)
            fakeUserDto.password = podamFactory.manufacturePojo(String::class.java)
            fakeUserDto.email = podamFactory.manufacturePojo(String::class.java)
            return fakeUserDto
        }
    }

    @Autowired
    private lateinit var signupService: SignupService

    @Autowired
    private lateinit var userMapper: UserMapper

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder


    @Test
    fun `Should create a new user`() {
        // given
        val fakeUser = userMapper.dtoToUser(createFakeUserDto())

        // when
        val createdUser = signupService.signup(fakeUser).block()

        // then
        Assertions.assertNotNull(createdUser)
        createdUser!!
        Assertions.assertNotNull(createdUser.createdDate)
        Assertions.assertNotNull(createdUser.lastModifiedDate)
        Assertions.assertEquals(fakeUser.username, createdUser.username)
        Assertions.assertTrue(passwordEncoder.matches(fakeUser.password, createdUser.password))
        Assertions.assertEquals(fakeUser.email, createdUser.email)
    }

    @Test
    fun `Should throw an error if the user already exists`() {
        // given
        val fakeUser = userMapper.dtoToUser(createFakeUserDto())
        signupService.signup(fakeUser).block()

        // when
        val createdUserMono = signupService.signup(fakeUser)

        // then
        StepVerifier.create(createdUserMono)
            .expectError(UserAlreadyExistsException::class.java)
            .verify()
    }

}