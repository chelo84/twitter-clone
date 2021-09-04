package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.shell.ShellHelper
import reactor.core.Disposable

abstract class Handler<P : HandlerProperties>(private val shellHandler: ShellHelper, val properties: P) : Disposable

class DefaultHandler(
    shellHandler: ShellHelper,
    properties: DefaultProperties,
) : Handler<DefaultProperties>(shellHandler, properties) {
    override fun dispose() {}
}

/**
 * Used when the [Handler] has no properties.
 */
class DefaultProperties : HandlerProperties

/**
 * [Handler] constructor argument
 */
interface HandlerProperties
