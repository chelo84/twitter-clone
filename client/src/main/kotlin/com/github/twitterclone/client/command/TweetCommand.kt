package com.github.twitterclone.client.command

import com.github.twitterclone.client.rsocket.factory.TweetRSocketReqFactory
import com.github.twitterclone.client.service.TweetService
import com.github.twitterclone.client.shell.InputReader
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.client.state.TweetState
import com.github.twitterclone.sdk.domain.user.User
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption


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
            value = ["--more", "-m"],
            help = "Used to search for more tweets from the user (ignored if username is present)"
        ) more: Boolean,
    ) {
        shellHelper.printInfo("Getting tweets from $username (more? $more)...")
        username?.also {
            if (tweetService.isConnectedToUser(it).not()) {
                shellHelper.printWarning("username not equals")
                tweetService.connectToTweets(username)
            }
        } ?: run {
            tweetRSocketReqFactory.dispose()
        }

        shellHelper.printInfo("... Find tweets ...")
    }

    @Suppress("NAME_SHADOWING")
    @ShellMethod(value = "Post a new tweet")
    fun tweet(
        @ShellOption(
            value = ["--text", "-t"],
            help = "Tweet's text",
            defaultValue = ShellOption.NULL
        ) text: String?,
    ) {
        val text = text ?: InputReader.INSTANCE.promptNotEmpty("Please enter the tweet's text", "text")
        shellHelper.printInfo("Posting tweet with text: $text")

        tweetService.postTweet(text)
    }
}