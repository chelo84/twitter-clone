package com.github.twitterclone.client.config

import com.github.twitterclone.client.shell.InputReader
import com.github.twitterclone.client.shell.PromptColor
import com.github.twitterclone.client.shell.ShellHelper
import org.jline.reader.History
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.Parser
import org.jline.terminal.Terminal
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.EventListener
import org.springframework.shell.jline.JLineShellAutoConfiguration


@Configuration
class SpringShellConfig {

    @Bean
    fun shellHelper(@Lazy terminal: Terminal): ShellHelper =
        ShellHelper(terminal)

    @Bean
    fun inputReader(
        @Lazy terminal: Terminal,
        @Lazy parser: Parser,
        completer: JLineShellAutoConfiguration.CompleterAdapter,
        @Lazy history: History,
        shellHelper: ShellHelper,
    ): InputReader {
        val lineReaderBuilder = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(completer)
            .history(history)
            .highlighter { _, buffer ->
                AttributedString(buffer, AttributedStyle.BOLD.foreground(PromptColor.WHITE.toJlineAttributedStyle()))
            }
            .parser(parser)

        val lineReader = lineReaderBuilder.build()
        lineReader.unsetOpt(LineReader.Option.INSERT_TAB)

        return InputReader(lineReader, shellHelper)
    }

    @EventListener(ApplicationStartedEvent::class)
    fun onStartup() {
        println("You can now enter commands. Type 'help' to see all commands available.")
    }
}