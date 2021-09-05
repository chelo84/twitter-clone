package com.github.twitterclone.client.shell

import org.jline.reader.impl.LineReaderImpl
import org.jline.terminal.Terminal
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.springframework.beans.factory.annotation.Value

class ShellHelper(
    val terminal: Terminal,
    private val lineReader: LineReaderImpl,
) {
    @Value("\${shell.out.info}")
    var infoColor: String? = null

    @Value("\${shell.out.success}")
    var successColor: String? = null

    @Value("\${shell.out.warning}")
    var warningColor: String? = null

    @Value("\${shell.out.error}")
    var errorColor: String? = null

    /**
     * Construct colored message in the given color.
     *
     * @param message message to return
     * @param color   color to print
     * @return colored message
     */
    fun getColored(message: String?, color: PromptColor): AttributedString {
        return AttributedStringBuilder()
            .append(message, AttributedStyle.DEFAULT.foreground(color.toJlineAttributedStyle()))
            .toAttributedString()
    }

    fun getInfoMessage(message: String?): String {
        return getColored(message, PromptColor.valueOf(infoColor!!)).toAnsi()
    }

    fun getSuccessMessage(message: String?): String {
        return getColored(message, PromptColor.valueOf(successColor!!)).toAnsi()
    }

    fun getWarningMessage(message: String?): String {
        return getColored(message, PromptColor.valueOf(warningColor!!)).toAnsi()
    }

    fun getErrorMessage(message: String?): String {
        return getColored(message, PromptColor.valueOf(errorColor!!)).toAnsi()
    }

    fun printSuccess(message: String?, above: Boolean = false) {
        print(message, PromptColor.valueOf(successColor!!), above)
    }

    fun printInfo(message: String?, above: Boolean = false) {
        print(message, PromptColor.valueOf(infoColor!!), above)
    }

    fun printWarning(message: String?, above: Boolean = false) {
        print(message, PromptColor.valueOf(warningColor!!), above)
    }

    fun printError(message: String?, above: Boolean = false) {
        print(message, PromptColor.valueOf(errorColor!!), above)
    }

    fun print(message: String?, color: PromptColor? = null, above: Boolean = false) {
        var toPrint = message
        color?.let {
            toPrint = getColored(message, color).toAnsi()
        }

        if (above) {
            lineReader.printAbove(toPrint)
        } else {
            terminal.writer().println(toPrint)
            terminal.flush()
        }
    }
}