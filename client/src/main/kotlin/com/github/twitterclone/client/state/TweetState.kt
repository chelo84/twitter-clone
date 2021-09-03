package com.github.twitterclone.client.state

import org.springframework.stereotype.Component

@Component
class TweetState(val currentPage: Int = 0)