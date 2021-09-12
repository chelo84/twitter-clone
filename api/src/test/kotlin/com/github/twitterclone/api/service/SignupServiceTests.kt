package com.github.twitterclone.api.service

import com.github.twitterclone.api.exception.UserAlreadyExistsException
import com.github.twitterclone.api.mapper.UserMapper
import com.github.twitterclone.sdk.domain.user.NewUser
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
        fun createFakeUserDto(): NewUser {
            val password = podamFactory.manufacturePojo(String::class.java)
            return NewUser(
                name = podamFactory.manufacturePojo(String::class.java),
                surname = podamFactory.manufacturePojo(String::class.java),
                username = podamFactory.manufacturePojo(String::class.java),
                password = password,
                passwordConfirmation = password,
                email = podamFactory.manufacturePojo(String::class.java)
            )
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
        val fakeNewUser = createFakeUserDto()

        // when
        val createdUser = signupService.signup(userMapper.newUserToUser(fakeNewUser)).block()

        // then
        Assertions.assertNotNull(createdUser)
        createdUser!!
        Assertions.assertNotNull(createdUser.createdDate)
        Assertions.assertNotNull(createdUser.lastModifiedDate)
        Assertions.assertEquals(fakeNewUser.username, createdUser.username)
        Assertions.assertTrue(passwordEncoder.matches(fakeNewUser.password, createdUser.password))
        Assertions.assertEquals(fakeNewUser.email, createdUser.email)
    }

    @Test
    fun `Should throw an error if the user already exists`() {
        // given
        val fakeUser = userMapper.newUserToUser(createFakeUserDto())
        signupService.signup(fakeUser).block()

        // when
        val createdUserMono = signupService.signup(fakeUser)

        // then
        StepVerifier.create(createdUserMono)
            .expectError(UserAlreadyExistsException::class.java)
            .verify()
    }

}