package com.github.twitterclone.client.command

import com.github.twitterclone.client.rsocket.RSocketRequesterName
import com.github.twitterclone.client.rsocket.RSocketRequesterRepository
import com.github.twitterclone.client.rsocket.handler.TweetsHandler
import com.github.twitterclone.client.service.TweetService
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.client.state.TweetState
import com.github.twitterclone.sdk.domain.user.User
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption


@ShellComponent
class TweetCommand(
    private val shellHelper: ShellHelper,
    private val rsocketRequesterRepository: RSocketRequesterRepository,
    private val tweetService: TweetService,
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
            val handler = rsocketRequesterRepository.getHandler(RSocketRequesterName.TWEETS) as TweetsHandler?
            if (handler?.username != username) {
                shellHelper.printWarning("username not equals")
                tweetService.connectToTweets(
                    username,
                    rsocketRequesterRepository.disposeAndCreate(RSocketRequesterName.TWEETS,
                                                                mapOf(Pair(TweetsHandler.TweetsArgument.USERNAME,
                                                                           username)))
                )
            }
        } ?: run {
            rsocketRequesterRepository.dispose(RSocketRequesterName.TWEETS)
        }

        shellHelper.printInfo("... Find tweets ...")
    }
}