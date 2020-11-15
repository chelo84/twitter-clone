package com.example.twitterclone.model.security

import org.springframework.security.oauth2.core.user.OAuth2User

abstract class UserInfo {
    lateinit var sub: String
    lateinit var name: String
    lateinit var email: String

    companion object {
        private val GOOGLE: String = "google".toLowerCase()
        private val GITHUB: String = "github".toLowerCase()
        private val FACEBOOK: String = "facebook".toLowerCase()

        fun from(registrationId: String, oAuth2User: OAuth2User): UserInfo {
            return when (registrationId.toLowerCase()) {
                GOOGLE -> GoogleUserInfo(oAuth2User.attributes)
                FACEBOOK -> FacebookUserInfo(oAuth2User.attributes)
                GITHUB -> GithubUserInfo(oAuth2User.attributes)
                else -> throw Exception("No UserInfo configured for Registration ID $registrationId")
            }
        }
    }
}

class GoogleUserInfo() : UserInfo() {
    constructor(attributes: Map<String, Any>) : this() {
        this.sub = attributes["sub"].toString().trim()
        this.name = attributes["name"].toString().trim()
        this.email = attributes["email"].toString().trim()
    }
}

class FacebookUserInfo() : UserInfo() {
    constructor(attributes: Map<String, Any>) : this() {
        this.sub = attributes["id"].toString().trim()
        this.name = attributes["name"].toString().trim()
        this.email = attributes["email"].toString().trim()
    }
}

class GithubUserInfo() : UserInfo() {
    constructor(attributes: Map<String, Any>) : this() {
        this.sub = attributes["id"].toString().trim()
        this.name = attributes["email"].toString().trim()
        this.email = attributes["email"].toString().trim()
    }
}