package com.github.twitterclone.client.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class ClientAuthenticationToken(private val principal: Any, private val credentials: Any) :
    AbstractAuthenticationToken(listOf()) {

    init {
        this.isAuthenticated = true
    }

    override fun getCredentials(): Any = credentials
    override fun getPrincipal(): Any = principal
}