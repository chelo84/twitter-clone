package com.github.twitterclone.server.exception

class UserAlreadyExistsException(username: String) : Exception("User $username already exists.")