package com.github.twitterclone.client.config

import com.github.twitterclone.client.shell.InputReader
import com.github.twitterclone.client.shell.ShellHelper
import org.jline.reader.History
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.Parser
import org.jline.reader.impl.LineReaderImpl
import org.jline.terminal.Terminal
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.EventListener
import org.springframework.shell.jline.JLineShellAutoConfiguration


@Configuration
class SpringShellConfig {

    @Bean
    fun shellHelper(
        @Lazy terminal: Terminal,
        @Lazy lineReader: LineReaderImpl,
    ): ShellHelper =
        ShellHelper(terminal, lineReader)

    @Bean
    fun inputReader(
        terminal: Terminal,
        parser: Parser,
        completer: JLineShellAutoConfiguration.CompleterAdapter,
        @Lazy history: History,
        shellHelper: ShellHelper,
    ): InputReader {
        val lineReaderBuilder = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(completer)
            .history(history)
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