package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.shell.ShellHelper
import reactor.core.Disposable

abstract class Handler(private val shellHelper: ShellHelper, private val args: Map<out HandlerArgument, Any>) :
    Disposable

/**
 * Used in [Enum] containing all arguments the implementation of [Handler] receives in its constructor
 */
interface HandlerArgument {
    val value: String
}