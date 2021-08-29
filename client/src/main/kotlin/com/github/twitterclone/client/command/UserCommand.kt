package com.github.twitterclone.client.command

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellMethodAvailability

@ShellComponent
class UserCommand : SecuredCommand() {

    @ShellMethodAvailability("isUserSignedIn")
    @ShellMethod(key = ["user-list"], value = "Display list of users")
    fun userList(): String {
        return "list"
    }
}