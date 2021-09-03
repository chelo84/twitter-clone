package com.github.twitterclone.client.service

import com.github.twitterclone.client.rsocket.RSocketRequesterWrapper
import com.github.twitterclone.client.rsocket.handler.FollowHandler
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.follow.Follow
import io.rsocket.metadata.WellKnownMimeType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class FollowService(
    private val shellHelper: ShellHelper,
) {

    /**
     * Observe new follows and unfollows to the signed-in user
     */
    fun connectToFollow(
        authentication: Authentication,
        rsocketRequesterWrapper: RSocketRequesterWrapper,
    ) {
        rsocketRequesterWrapper.rsocketRequester
            .route("follow")
            .metadata(
                BearerTokenMetadata(authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .sendMetadata()
            .doOnSuccess {
                val handler = rsocketRequesterWrapper.handler as FollowHandler
                handler.getFollows()
                    .subscribe { user ->
                        shellHelper.printWarning("User <${user.username}> just followed you!!", above = true)
                    }
                handler.getUnfollows()
                    .subscribe { user ->
                        shellHelper.printWarning("User <${user.username}> unfollowed you D:", above = true)
                    }
            }
            .subscribe()
    }

    fun follow(
        username: String,
        rsocketRequesterWrapper: RSocketRequesterWrapper,
    ) {
        rsocketRequesterWrapper.rsocketRequester
            .route("follow")
            .metadata(
                BearerTokenMetadata(SecurityContextHolder.getContext().authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(username)
            .retrieveMono(Follow::class.java)
            .doOnNext { follow ->
                shellHelper.printSuccess("Successfully followed ${follow.pair.followed}",
                                         above = true)
            }
            .onErrorResume {
                shellHelper.printError("Couldn't follow: ${it.message}", above = true).toMono()
                    .then(Mono.empty())
            }
            .subscribe()
    }

    fun unfollow(
        username: String,
        rsocketRequesterWrapper: RSocketRequesterWrapper,
    ) {
        rsocketRequesterWrapper.rsocketRequester
            .route("unfollow")
            .metadata(
                BearerTokenMetadata(SecurityContextHolder.getContext().authentication.credentials as String),
                MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
            )
            .data(username)
            .retrieveMono(Void::class.java)
            .doOnSuccess { shellHelper.printSuccess("Successfully unfollowed $username", above = true) }
            .onErrorResume { shellHelper.printError("Couldn't unfollow: ${it.message}", above = true).toMono().then() }
            .subscribe()
    }
}