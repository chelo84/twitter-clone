package com.github.twitterclone.client.command

import com.github.twitterclone.client.rsocket.RSocketRequesterName
import com.github.twitterclone.client.rsocket.RSocketRequesterRepository
import com.github.twitterclone.client.service.FollowService
import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.user.User
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption


@ShellComponent
class FollowCommand(
    private val shellHelper: ShellHelper,
    private val rSocketRequesterRepository: RSocketRequesterRepository,
    private val followService: FollowService,
) : SecuredCommand() {

    /**
     * Follow a [username]
     *
     * This action notifies the [username] followed
     * @param username User's username
     */
    @ShellMethod(value = "Follow a user")
    fun follow(@ShellOption(value = ["--username", "-u"], help = "User's username to follow") username: String) {
        shellHelper.printInfo("following $username ...")
        followService.follow(username, rSocketRequesterRepository.get(RSocketRequesterName.FOLLOW))
    }

    /**
     * Unfollow a [username]
     *
     * This action notifies the [username] unfollowed
     * @param username [User.username]
     */
    @ShellMethod(value = "Unfollow a user")
    fun unfollow(@ShellOption(value = ["--username", "-u"], help = "User's username to unfollow") username: String) {
        shellHelper.printInfo("unfollowing $username ...")
        followService.unfollow(username, rSocketRequesterRepository.get(RSocketRequesterName.FOLLOW))
    }
}