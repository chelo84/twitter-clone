package com.github.twitterclone.exception

class UserAlreadyExistsException(username: String) : Exception("User $username already exists.")