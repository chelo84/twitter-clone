package com.github.twitterclone.rsocket

import com.github.twitterclone.config.Log
import com.github.twitterclone.mapper.UserMapper
import com.github.twitterclone.model.document.user.User
import com.github.twitterclone.security.jwt.JWTTokenService
import com.github.twitterclone.service.SignupService
import com.github.twitterclone.service.SignupServiceTests
import io.rsocket.SocketAcceptor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.annotation.DirtiesContext
import uk.co.jemos.podam.api.PodamFactoryImpl


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
abstract class TwitterCloneTests {
    companion object : Log() {
        val podamFactory = PodamFactoryImpl()
    }

    @Autowired
    private lateinit var userMapper: UserMapper

    @Autowired
    private lateinit var signupService: SignupService

    @Autowired
    protected lateinit var builder: RSocketRequester.Builder

    @Autowired
    protected lateinit var messageHandler: RSocketMessageHandler

    @Autowired
    protected lateinit var strategies: RSocketStrategies

    protected lateinit var fakeAuthentication: FakeAuthentication

    @BeforeEach
    fun setup() {
        val fakeUser = newFakeUserAndToken().first

        val authorities = mutableListOf<GrantedAuthority>()
        authorities.addAll((0..5).map {
            @Suppress("UNCHECKED_CAST")
            podamFactory.manufacturePojo(SimpleGrantedAuthority::class.java)
        })

        val token = JWTTokenService.generateToken(fakeUser.username, authorities)
        fakeAuthentication = FakeAuthentication(
                fakeUser,
                authorities,
                token
        )
    }

    final fun newFakeUserAndToken(): Pair<User, String> {
        val fakeUser = userMapper.dtoToUser(SignupServiceTests.createFakeUserDto())
        return Pair(signupService.signup(fakeUser).block()!!,
                    JWTTokenService.generateToken(fakeUser.username, listOf()))
    }

    fun createRSocketRequester(): RSocketRequester {
        return createRSocketRequester(null)
    }

    fun createRSocketRequester(handler: Any?): RSocketRequester {
        var responder: SocketAcceptor? = null
        if (handler != null) {
            responder = RSocketMessageHandler.responder(strategies, handler)
        }
        return builder
                .rsocketConnector { if (responder != null) it.acceptor(responder) }
                .tcp("localhost", 7000)
    }
}

data class FakeAuthentication(
        val principal: User,
        val authorities: List<GrantedAuthority>,
        val token: String,
)