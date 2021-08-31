package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.shell.ShellHelper

abstract class Handler(private val shellHelper: ShellHelper, private val args: Map<out HandlerArgument, Any>)

/**
 * Used in [Enum] containing all arguments the implementation of [Handler] receives in its constructor
 */
interface HandlerArgument {
    val value: String
}