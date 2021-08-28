package com.github.twitterclone.client.command

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.shell.Availability

abstract class SecuredCommand {

    fun isUserSignedIn(): Availability {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication

        return when {
            authentication != null -> Availability.available()
            else -> Availability.unavailable("you are not signed in. Please sign in!")
        }
    }
}