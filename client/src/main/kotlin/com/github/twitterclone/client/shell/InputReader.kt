package com.github.twitterclone.client.shell

import org.jline.reader.LineReader
import org.springframework.util.StringUtils

class InputReader(private val lineReader: LineReader, private val shellHelper: ShellHelper, mask: Char?) {
    private val mask: Char

    constructor(lineReader: LineReader, shellHelper: ShellHelper) : this(lineReader, shellHelper, null)

    /**
     * Prompts user for input.
     *
     * @param prompt
     * @param defaultValue
     * @param echo
     * @return User inputted [String]
     */
    @JvmOverloads
    fun prompt(prompt: String, defaultValue: String? = null, echo: Boolean = true): String? {
        val answer: String? = if (echo) {
            lineReader.readLine("$prompt: ")
        } else {
            lineReader.readLine("$prompt: ", mask)
        }
        return if (!StringUtils.hasText(answer)) {
            defaultValue
        } else answer
    }

    @JvmOverloads
    fun promptNotEmpty(prompt: String, propertyName: String, echo: Boolean = true): String =
        promptWithPredicate(prompt, StringUtils::hasText, "${propertyName.capitalize()} can not be empty!", echo)

    @JvmOverloads
    fun promptWithPredicate(
        prompt: String,
        predicate: (String?) -> Boolean,
        onInvalidMessage: String?,
        echo: Boolean = true
    ): String {
        var answer: String?
        var answerInvalid = true
        do {
            answer = this.prompt(prompt, null, echo)
            if (predicate(answer)) {
                answerInvalid = false
            } else {
                shellHelper.printWarning(onInvalidMessage ?: "Answer can not be empty")
            }
        } while (answerInvalid)

        return answer!!
    }

    /**
     * Loops until one of the `options` is provided. Pressing return is equivalent to
     * returning `defaultValue`.
     * <br></br>
     * Passing null for defaultValue signifies that there is no default value.<br></br>
     * Passing "" or null among optionsAsList means that empty answer is allowed, in these cases this method returns
     * empty String "" as the result of its execution.
     *
     *
     */
    fun promptWithOptions(prompt: String?, defaultValue: String?, optionsAsList: List<String>): String? {
        var answer: String?
        val allowedAnswers: MutableList<String?> = ArrayList(optionsAsList)
        if (!StringUtils.hasText(defaultValue)) {
            allowedAnswers.add("")
        }
        do {
            answer = lineReader.readLine(
                java.lang.String.format(
                    "%s %s: ",
                    prompt,
                    formatOptions(defaultValue, optionsAsList)
                )
            )
        } while (!allowedAnswers.contains(answer) && "" != answer)
        return if (!StringUtils.hasText(answer) && allowedAnswers.contains("")) {
            defaultValue
        } else answer
    }

    private fun formatOptions(defaultValue: String?, optionsAsList: List<String?>): List<String?> {
        val result: MutableList<String?> = ArrayList()
        for (option in optionsAsList) {
            var `val` = option
            if ("" == option || option == null) {
                `val` = "''"
            }
            if (defaultValue != null) {
                if (defaultValue == option || defaultValue == "" && option == null) {
                    `val` = shellHelper.getInfoMessage(`val`)
                }
            }
            result.add(`val`)
        }
        return result
    }

    /**
     * Loops until one value from the list of options is selected, printing each option on its own line.
     *
     */
    fun selectFromList(
        headingMessage: String?,
        promptMessage: String?,
        options: Map<String?, String?>,
        ignoreCase: Boolean,
        defaultValue: String?
    ): String? {
        var answer: String?
        val allowedAnswers: MutableSet<String?> = options.keys.toMutableSet()
        if (defaultValue != null && defaultValue != "") {
            allowedAnswers.add("")
        }
        shellHelper.print(String.format("%s: ", headingMessage))
        do {
            for (option in options.entries) {
                var defaultMarker: String? = null
                if (defaultValue != null) {
                    if (option.key.equals(defaultValue)) {
                        defaultMarker = "*"
                    }
                }
                if (defaultMarker != null) {
                    shellHelper.printInfo(
                        java.lang.String.format(
                            "%s [%s] %s ",
                            defaultMarker,
                            option.key,
                            option.value
                        )
                    )
                } else {
                    shellHelper.print(java.lang.String.format("  [%s] %s", option.key, option.value))
                }
            }
            answer = lineReader.readLine(String.format("%s: ", promptMessage))
        } while (!containsString(allowedAnswers, answer, ignoreCase) && "" !== answer)
        return if (!StringUtils.hasText(answer) && allowedAnswers.contains("")) {
            defaultValue
        } else answer
    }

    private fun containsString(l: Set<String?>, s: String?, ignoreCase: Boolean): Boolean {
        if (!ignoreCase) {
            return l.contains(s)
        }
        val it = l.iterator()
        while (it.hasNext()) {
            if (it.next().equals(s, ignoreCase = true)) return true
        }
        return false
    }

    companion object {
        const val DEFAULT_MASK = '*'
    }

    init {
        this.mask = mask ?: DEFAULT_MASK
    }
}