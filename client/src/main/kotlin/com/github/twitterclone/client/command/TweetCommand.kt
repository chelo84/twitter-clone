package com.github.twitterclone.client.command

import com.github.twitterclone.client.rsocket.factory.TweetRSocketReqFactory
import com.github.twitterclone.client.service.TweetService
import com.github.twitterclone.client.shell.InputReader
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.client.state.TweetState
import com.github.twitterclone.sdk.domain.user.User
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import reactor.core.publisher.Flux


@ShellComponent
class TweetCommand(
    private val shellHelper: ShellHelper,
    private val tweetService: TweetService,
    private val tweetRSocketReqFactory: TweetRSocketReqFactory,
    private val tweetState: TweetState,
) : SecuredCommand() {

    /**
     * Query tweets from [username]
     * @param username [User.username]. _Signed-in user if absent_
     * @param more whether to load more data. _Ignored if username is present_
     */
    @ShellMethod(value = "Get tweets from a user")
    fun tweets(
        @ShellOption(
            value = ["--username", "-u"],
            help = "User's username",
            defaultValue = ShellOption.NULL
        ) username: String?,
        @ShellOption(
            value = ["--more", "-m", "more"],
            help = "Used to search for more tweets from the user (ignored if username is present)"
        ) more: Boolean,
    ) {
        val principal = SecurityContextHolder.getContext().authentication.principal as User

        if (more.not()) {
            username?.also {
                shellHelper.printWarning("username not equals")
                if (tweetService.isConnectedToUser(username).not()) {
                    tweetService.connectToTweets(username)
                        .doOnSuccess { tweetState.reset(username) }
                        .block()
                }
            } ?: run {
                tweetRSocketReqFactory.dispose()
                tweetState.reset(principal.username)
            }
        }

        tweetService.fetchTweets(tweetState.username!!, tweetState.currentPage + 1)
            .switchIfEmpty {
                val builder: AttributedStringBuilder =
                    AttributedStringBuilder().append("No tweets found", AttributedStyle.BOLD)
                shellHelper.print(builder.toAnsi(), above = true)
            }
            .doOnNext { tweet ->
                tweetService.getTweetToPrint(tweet)
                    .doOnNext { text ->
                        shellHelper.print(text.toAnsi(), above = true)
                    }
                    .subscribe()
            }
            .onErrorResume {
                shellHelper.printError("Error while fetching tweets: ${it.message}", above = true)
                Flux.empty()
            }
            .doOnComplete {
                tweetState.currentPage++
            }
            .subscribe()
    }

    @Suppress("NAME_SHADOWING")
    @ShellMethod(value = "Post a new tweet")
    fun tweet(
        @ShellOption(
            value = ["--text", "-t"],
            help = "Tweet's text",
            defaultValue = ShellOption.NULL
        ) text: String?,
        @ShellOption(
            value = ["--reply", "-r"],
            help = "Reply to a tweet (with its ID)",
            defaultValue = ShellOption.NULL
        ) replyTo: String?,
    ) {
        val text = text ?: InputReader.INSTANCE.promptNotEmpty("Please enter the tweet's text", "text")
        tweetService.postTweet(text, replyTo)
    }
}