package com.github.twitterclone.server.model.document.follow

class FollowPair() {

    lateinit var follower: String

    lateinit var followed: String

    constructor(follower: String, followed: String) : this() {
        this.follower = follower
        this.followed = followed
    }
}