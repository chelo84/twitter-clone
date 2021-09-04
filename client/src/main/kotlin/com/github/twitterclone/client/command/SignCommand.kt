package com.github.twitterclone.client.command

import com.github.twitterclone.client.service.FollowService
import com.github.twitterclone.client.service.UserService
import com.github.twitterclone.client.shell.InputReader
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.user.NewUser
import com.github.twitterclone.sdk.domain.user.User
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class SignCommand(
    private val shellHelper: ShellHelper,
    private val authenticationProvider: AuthenticationProvider,
    private val userService: UserService,
    private val followService: FollowService,
) {

    /**
     * Sign in with [username] and [password] to use [SecuredCommand] commands
     * @see [User]
     */
    @Suppress("NAME_SHADOWING")
    @ShellMethod("Sign in as user")
    fun signIn(
        @ShellOption(
            value = ["--username", "-u"],
            help = "Username",
            defaultValue = ShellOption.NULL
        ) username: String?,
        @ShellOption(
            value = ["--password", "-p"],
            help = "Password",
            defaultValue = ShellOption.NULL
        ) password: String?,
    ) {
        val username = username ?: InputReader.INSTANCE.promptNotEmpty("Please enter your username", "username")
        val password = password ?: InputReader.INSTANCE.promptNotEmpty("Please enter your password", "password", false)
        val request: Authentication = UsernamePasswordAuthenticationToken(username, password)

        try {
            val result: Authentication = authenticationProvider.authenticate(request)
            SecurityContextHolder.getContext().authentication = result
            followService.connectToFollow(result)
            shellHelper.printSuccess("Credentials successfully authenticated! $username -> welcome")
        } catch (ex: AuthenticationException) {
            shellHelper.printWarning("Authentication failed ${ex.message}")
        }
    }

    /**
     * Sign up a new [User]
     */
    @ShellMethod("Sign up user")
    fun signUp() {
        val username: String = InputReader.INSTANCE.promptNotEmpty("Please enter your username", "username")
        val password: String = InputReader.INSTANCE.promptNotEmpty("Please enter your password", "password")
        val passwordConfirmation: String = InputReader.INSTANCE.promptWithPredicate(
            prompt = "Please confirm the password",
            predicate = { p -> p == password },
            onInvalidMessage = "Password and confirmation do not match"
        )
        val name: String = InputReader.INSTANCE.promptNotEmpty("Please enter your name", "name")
        val surname: String = InputReader.INSTANCE.promptNotEmpty("Please enter your surname", "surname")
        val email: String = InputReader.INSTANCE.promptNotEmpty("Please enter your email", "email")

        shellHelper.printInfo("Creating user...")
        userService.signUp(
            NewUser(
                username = username,
                password = password,
                passwordConfirmation = passwordConfirmation,
                name = name,
                surname = surname,
                email = email
            )
        )
    }
}