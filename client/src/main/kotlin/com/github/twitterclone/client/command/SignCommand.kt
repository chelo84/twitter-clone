package com.github.twitterclone.client.command

import com.github.twitterclone.client.rsocket.RSocketRequesterFactory
import com.github.twitterclone.client.rsocket.RSocketRequesterName
import com.github.twitterclone.client.shell.InputReader
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.error.Error
import com.github.twitterclone.sdk.domain.user.NewUser
import com.github.twitterclone.sdk.domain.user.User
import io.rsocket.metadata.WellKnownMimeType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.util.MimeTypeUtils
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@ShellComponent
class SignCommand(
    private val shellHelper: ShellHelper,
    private val authenticationProvider: AuthenticationProvider,
    private val webClient: WebClient,
    private val rSocketRequesterFactory: RSocketRequesterFactory,
) {

    @Autowired
    lateinit var inputReader: InputReader

    @ShellMethod("Sign in as user")
    fun signIn() {
        val username = inputReader.promptNotEmpty("Please enter your username", "username")
        val password = inputReader.promptNotEmpty("Please enter your password", "password", false)
        val request: Authentication = UsernamePasswordAuthenticationToken(username, password)

        try {
            val result: Authentication = authenticationProvider.authenticate(request)
            SecurityContextHolder.getContext().authentication = result
            connectToFollow(result)
            shellHelper.printSuccess("Credentials successfully authenticated! $username -> welcome")
        } catch (ex: AuthenticationException) {
            shellHelper.printWarning("Authentication failed ${ex.message}")
        }
    }

    private fun connectToFollow(authentication: Authentication) {
        rSocketRequesterFactory.get(RSocketRequesterName.FOLLOW)
            .route("follow")
            .metadata(
                BearerTokenMetadata(authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .sendMetadata()
            .retry()
            .subscribe()
    }

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
        webClient.post()
            .uri("/signup")
            .bodyValue(
                NewUser(
                    username = username,
                    password = password,
                    passwordConfirmation = passwordConfirmation,
                    name = name,
                    surname = surname,
                    email = email
                )
            )
            .retrieve()
            .onStatus({
                          arrayOf(HttpStatus.Series.CLIENT_ERROR, HttpStatus.Series.SERVER_ERROR).contains(
                              it.series()
                          )
                      },
                      { resp ->
                          resp.bodyToMono<Error>()
                              .flatMap {
                                  Mono.error(Exception(it.message))
                              }
                      }
            )
            .bodyToMono(User::class.java)
            .doOnNext {
                shellHelper.printSuccess("User ${it.username} created! Use the command sign-up to enter", above = true)
            }
            .onErrorResume { err ->
                shellHelper.printError(err.message, above = true)
                Mono.empty()
            }
            .subscribe()
    }
}