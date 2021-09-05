package com.github.twitterclone.client.shell

import com.github.twitterclone.client.command.SecuredCommand
import com.github.twitterclone.sdk.domain.user.User
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component

@Component
class CustomPromptProvider : SecuredCommand(), PromptProvider {
    override fun getPrompt(): AttributedString {
        val builder = AttributedStringBuilder().append("shell:")
        if (isUserSignedIn().isAvailable) {
            val principal = SecurityContextHolder.getContext().authentication.principal as User
            builder.append(principal.username, AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN))
        } else {
            builder.append("unassigned", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
        }

        return builder.append("> ").toAttributedString()
    }
}