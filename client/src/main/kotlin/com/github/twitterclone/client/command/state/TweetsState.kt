package com.github.twitterclone.client.command.state

import org.springframework.stereotype.Component

@Component
class TweetsState(val currentPage: Int = 0)