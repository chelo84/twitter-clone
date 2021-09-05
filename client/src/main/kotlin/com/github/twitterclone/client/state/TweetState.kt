package com.github.twitterclone.client.state

import org.springframework.stereotype.Component

@Component
class TweetState(var currentPage: Int = 0)