package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.shell.ShellHelper
import reactor.core.Disposable

abstract class Handler(private val shellHelper: ShellHelper, private val args: Map<out HandlerArgument, Any>) :
    Disposable

/**
 * Implement this class to add custom arguments to the [Handler] constructor
 */
interface HandlerArgument {
    val value: String
}