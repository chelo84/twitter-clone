package com.github.twitterclone.client.command

import com.github.twitterclone.client.shell.ShellHelper
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellMethodAvailability


@ShellComponent
class FollowCommand(private val shellHelper: ShellHelper) : SecuredCommand() {

    @ShellMethod(value = "Follow a user")
    @ShellMethodAvailability("isUserSignedIn")
    fun follow() {
        shellHelper.printInfo("follow -> ...")
    }
}