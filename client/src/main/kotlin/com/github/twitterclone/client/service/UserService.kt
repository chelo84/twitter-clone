package com.github.twitterclone.client.service

import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.error.Error
import com.github.twitterclone.sdk.domain.user.NewUser
import com.github.twitterclone.sdk.domain.user.User
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Service
class UserService(
    private val webClient: WebClient,
    private val shellHelper: ShellHelper,
) {

    fun signUp(newUser: NewUser) {
        webClient.post()
            .uri("/signup")
            .bodyValue(newUser)
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