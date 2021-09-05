package com.github.twitterclone.client.state

import org.springframework.stereotype.Component

@Component
class TweetState(var username: String?, var currentPage: Int = -1) {

    fun reset(username: String) {
        this.username = username
        currentPage = -1
    }
}