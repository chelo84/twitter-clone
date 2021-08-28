package com.github.twitterclone.server.exception

class UserNotFoundException(id: String) : Exception("User with username $id not found")