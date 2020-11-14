package com.example.oauthdemo.rsocket

import com.example.oauthdemo.mapper.UserMapper
import com.example.oauthdemo.model.document.user.User
import com.example.oauthdemo.security.jwt.JWTTokenService
import com.example.oauthdemo.service.SignupService
import com.example.oauthdemo.service.SignupServiceTests
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.annotation.DirtiesContext
import uk.co.jemos.podam.api.PodamFactoryImpl

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
abstract class ZakoomTests {

    @Autowired
    private lateinit var userMapper: UserMapper

    @Autowired
    private lateinit var signupService: SignupService

    protected lateinit var fakePrincipal: FakePrincipal

    companion object {
        val podamFactory = PodamFactoryImpl()
    }

    @BeforeAll
    fun setup() {
        val fakeUser = newFakeUser()

        val authorities = mutableListOf<GrantedAuthority>()
        authorities.addAll((0..5).map {
            @Suppress("UNCHECKED_CAST")
            podamFactory.manufacturePojo(SimpleGrantedAuthority::class.java)
        })

        val token = JWTTokenService.generateToken(fakeUser.username, fakeUser, authorities)
        fakePrincipal = FakePrincipal(
                fakeUser,
                authorities,
                token
        )
    }

    final fun newFakeUser(): User {
        val fakeUser = userMapper.dtoToUser(SignupServiceTests.createFakeUserDto())
        return signupService.signup(fakeUser).block()!!
    }
}

data class FakePrincipal(
        val principal: User,
        val authorities: List<GrantedAuthority>,
        val token: String,
)