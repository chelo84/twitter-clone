package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.shell.ShellHelper

/**
 * Default [Handler] used for trivial RSocket requests
 */
class DefaultHandler(shellHelper: ShellHelper, args: Map<out HandlerArgument, Any>) : Handler(shellHelper, args)