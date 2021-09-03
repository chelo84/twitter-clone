package com.github.twitterclone.client.command

import com.github.twitterclone.client.rsocket.RSocketRequesterName
import com.github.twitterclone.client.rsocket.RSocketRequesterRepository
import com.github.twitterclone.client.service.FollowService
import com.github.twitterclone.client.service.UserService
import com.github.twitterclone.client.shell.InputReader
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.user.NewUser
import com.github.twitterclone.sdk.domain.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class SignCommand(
    private val shellHelper: ShellHelper,
    private val authenticationProvider: AuthenticationProvider,
    private val userService: UserService,
    private val rsocketRequesterFactory: RSocketRequesterRepository,
    private val followService: FollowService,
) {

    @Autowired
    lateinit var inputReader: InputReader

    /**
     * Sign in with [User.username] and [User.password] to use [SecuredCommand] commands
     * @see [User]
     */
    @ShellMethod("Sign in as user")
    fun signIn() {
        val username = inputReader.promptNotEmpty("Please enter your username", "username")
        val password = inputReader.promptNotEmpty("Please enter your password", "password", false)
        val request: Authentication = UsernamePasswordAuthenticationToken(username, password)

        try {
            val result: Authentication = authenticationProvider.authenticate(request)
            SecurityContextHolder.getContext().authentication = result
            followService.connectToFollow(result, rsocketRequesterFactory.get(RSocketRequesterName.FOLLOW))
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
        val username: String = inputReader.promptNotEmpty("Please enter your username", "username")
        val password: String = inputReader.promptNotEmpty("Please enter your password", "password")
        val passwordConfirmation: String = inputReader.promptWithPredicate(
            prompt = "Please confirm the password",
            predicate = { p -> p == password },
            onInvalidMessage = "Password and confirmation do not match"
        )
        val name: String = inputReader.promptNotEmpty("Please enter your name", "name")
        val surname: String = inputReader.promptNotEmpty("Please enter your surname", "surname")
        val email: String = inputReader.promptNotEmpty("Please enter your email", "email")

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