package com.example.oauthdemo.service.security

import com.example.oauthdemo.model.document.User
import com.example.oauthdemo.model.security.UserInfo
import com.example.oauthdemo.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import reactor.core.publisher.Mono
import java.util.*


class CustomReactiveOAuth2UserService{ /*: ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private val delegate = DefaultReactiveOAuth2UserService()

    companion object {
        const val ZAKOOM_UID = "zakoom-uid"
    }

    @Autowired
    private lateinit var userService: UserService

    override fun loadUser(userRequest: OAuth2UserRequest?): Mono<OAuth2User> {
        val registrationId = userRequest?.clientRegistration?.registrationId as String

        return delegate.loadUser(userRequest)
                .map { oAuth2User ->
                    val userInfo: UserInfo = UserInfo.from(registrationId, oAuth2User)
                    val user: User = userService.findNonReactive(userInfo.sub, userInfo.email)
                            ?: userService.createNonReactive(userInfo)
                    val attributes = HashMap(oAuth2User.attributes).apply { this[ZAKOOM_UID] = user.id }

                    // Discover what's the nameAttributeKey
                    val nameAttributeKey: String = attributes.filterValues { it == oAuth2User.name }
                            .keys
                            .toList()[0]

                    DefaultOAuth2User(oAuth2User.authorities, attributes, nameAttributeKey)
                }
    }*/
}