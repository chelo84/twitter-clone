package com.github.twitterclone.client.shell

import org.jline.terminal.Terminal
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.springframework.beans.factory.annotation.Value

class ShellHelper(
    var terminal: Terminal
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
    fun getColored(message: String?, color: PromptColor): String {
        return AttributedStringBuilder()
            .append(message, AttributedStyle.DEFAULT.foreground(color.toJlineAttributedStyle())).toAnsi()
    }

    fun getInfoMessage(message: String?): String {
        return getColored(message, PromptColor.valueOf(infoColor!!))
    }

    fun getSuccessMessage(message: String?): String {
        return getColored(message, PromptColor.valueOf(successColor!!))
    }

    fun getWarningMessage(message: String?): String {
        return getColored(message, PromptColor.valueOf(warningColor!!))
    }

    fun getErrorMessage(message: String?): String {
        return getColored(message, PromptColor.valueOf(errorColor!!))
    }

    /**
     * Print message to the console in the success color.
     *
     * @param message message to print
     */
    fun printSuccess(message: String?) {
        print(message, PromptColor.valueOf(successColor!!))
    }

    /**
     * Print message to the console in the info color.
     *
     * @param message message to print
     */
    fun printInfo(message: String?) {
        print(message, PromptColor.valueOf(infoColor!!))
    }

    /**
     * Print message to the console in the warning color.
     *
     * @param message message to print
     */
    fun printWarning(message: String?) {
        print(message, PromptColor.valueOf(warningColor!!))
    }

    /**
     * Print message to the console in the error color.
     *
     * @param message message to print
     */
    fun printError(message: String?) {
        print(message, PromptColor.valueOf(errorColor!!))
    }

    @JvmOverloads
    fun print(message: String?, color: PromptColor? = null) {
        var toPrint = message
        if (color != null) {
            toPrint = getColored(message, color)
        }
        terminal.writer().println(toPrint)
        terminal.flush()
    }
}