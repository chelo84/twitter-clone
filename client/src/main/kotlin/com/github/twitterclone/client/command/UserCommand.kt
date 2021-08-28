package com.github.twitterclone.client.command

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellMethodAvailability

@ShellComponent
class UserCommand : SecuredCommand() {

    @ShellMethod("Display list of users")
    @ShellMethodAvailability("isUserSignedIn")
    fun userList(): String {
        println("available")
        return "list"
    }
}