package com.github.twitterclone.server.service

import com.github.twitterclone.server.model.security.GoogleUserInfo
import com.github.twitterclone.server.model.security.UserInfo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.context.SpringBootTest
import uk.co.jemos.podam.api.PodamFactoryImpl


@SpringBootTest
@ExtendWith(MockitoExtension::class)
class UserServiceTest {
    private val factory = PodamFactoryImpl()

//    @Autowired
//    private lateinit var userService: UserService

    private fun createFakeUserInfo(): UserInfo {
        return factory.manufacturePojo(GoogleUserInfo::class.java)
    }

    @Test
    fun `Should create a new User (Non reactive)`() {
//        // given
//        val fakeUserInfo = createFakeUserInfo()
//
//        // when
//        val createdUser = userService.createNonReactive(fakeUserInfo)
//
//        // then
//        assertNotNull(createdUser)
////        assertEquals(createdUser.sub, fakeUserInfo.sub)
////        assertEquals(createdUser.name, fakeUserInfo.name)
////        assertEquals(createdUser.email, fakeUserInfo.email)
    }

    @Test
    fun `Should create a new User`() {
//        // given
//        val fakeUserInfo = createFakeUserInfo()
//
//        // when
//        val createdUser = userService.create(fakeUserInfo).block()
//
//        // then
//        assertNotNull(createdUser)
////        assertEquals(createdUser!!.sub, fakeUserInfo.sub)
////        assertEquals(createdUser.name, fakeUserInfo.name)
////        assertEquals(createdUser.email, fakeUserInfo.email)
    }

    @Test
    fun `Should throw error if the User already exists (Non reactive)`() {
//        // given
//        val fakeUserInfo = createFakeUserInfo()
//        userService.createNonReactive(fakeUserInfo)
//
//        // then
//        assertThrows(UserAlreadyExistsException::class.java) { userService.createNonReactive(fakeUserInfo) }
    }

    @Test
    fun `Should throw error if the User already exists`() {
        // given
//        val fakeUserInfo = createFakeUserInfo()
//        userService.create(fakeUserInfo).block()
//
//        // when
//        val createdUserMono = userService.create(fakeUserInfo)
//
//        // then
//        StepVerifier.create(createdUserMono)
//                .expectError(UserAlreadyExistsException::class.java)
//                .verify()
    }

    @Test
    fun `Should link the User to a Community`() {
//        TODO()
    }

    @Test
    fun `Should list the User's Community list`() {
//        TODO()
    }
}