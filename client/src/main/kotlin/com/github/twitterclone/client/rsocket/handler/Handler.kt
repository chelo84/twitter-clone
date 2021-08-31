package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.shell.ShellHelper

abstract class Handler(private val shellHelper: ShellHelper, private val args: Map<out HandlerArgument, Any>)

interface HandlerArgument {
    val value: String
}