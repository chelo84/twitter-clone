package com.github.twitterclone.client.rsocket.handler

import com.github.twitterclone.client.shell.ShellHelper

interface HandlerFactory<H, P> {
    fun createHandler(
        shellHelper: ShellHelper,
        properties: P,
    ): H
}