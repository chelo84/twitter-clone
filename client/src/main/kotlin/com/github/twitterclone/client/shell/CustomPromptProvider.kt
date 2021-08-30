package com.github.twitterclone.client.shell

import org.jline.utils.AttributedString
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component

@Component
class CustomPromptProvider : PromptProvider {
    override fun getPrompt(): AttributedString = AttributedString("shell:> ")
}