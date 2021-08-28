package com.github.twitterclone.client.command

import com.github.twitterclone.client.shell.InputReader
import com.github.twitterclone.client.shell.ShellHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.util.StringUtils

@ShellComponent
class SignInCommand : SecuredCommand() {

    @Autowired
    lateinit var shellHelper: ShellHelper

    @Autowired
    lateinit var inputReader: InputReader

    @Autowired
    lateinit var authenticationProvider: AuthenticationProvider

    @ShellMethod("Sign in as clidemo user")
    fun signIn() {
        var username: String?
        var usernameInvalid = true

        do {
            username = inputReader.prompt("Please enter your username")
            println(username)
            if (StringUtils.hasText(username)) {
                usernameInvalid = false
            } else {
                shellHelper.printWarning("Username can not be empty!")
            }
        } while (usernameInvalid)
        val password = inputReader.prompt("Please enter your password", null, false)
        val request: Authentication = UsernamePasswordAuthenticationToken(username, password)

        try {
            val result: Authentication = authenticationProvider.authenticate(request)
            SecurityContextHolder.getContext().authentication = result
            shellHelper.printSuccess("Credentials successfully authenticated! $username -> welcome")
        } catch (ex: AuthenticationException) {
            shellHelper.printWarning("Authentication failed ${ex.message}")
        }
    }
}