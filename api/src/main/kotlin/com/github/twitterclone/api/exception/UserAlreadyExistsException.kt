package com.github.twitterclone.api.exception

class UserAlreadyExistsException(username: String) : Exception("User $username already exists.")