package com.github.twitterclone.client.command

import com.github.twitterclone.client.shell.ShellHelper
import com.github.twitterclone.sdk.domain.user.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellMethodAvailability
import org.springframework.shell.table.BeanListTableModel
import org.springframework.shell.table.BorderStyle
import org.springframework.shell.table.TableBuilder


@ShellComponent
class ProfileCommand(private val shellHelper: ShellHelper) : SecuredCommand() {

    @ShellMethodAvailability("isUserSignedIn")
    @ShellMethod(value = "Display list of users")
    fun showProfile() {
        val userTableModel = BeanListTableModel(
            listOf(SecurityContextHolder.getContext().authentication.principal as User),
            linkedMapOf(
                Pair("id", "ID"),
                Pair("username", "Username"),
                Pair("name", "Name"),
                Pair("surname", "Surname"),
                Pair("createdDate", "Created on"),
            )
        )
        val table = TableBuilder(userTableModel)
            .addInnerBorder(BorderStyle.fancy_light)
            .addHeaderBorder(BorderStyle.fancy_double)
            .build()

        shellHelper.printInfo("Profile:")
        shellHelper.print(table.render(200))
    }
}