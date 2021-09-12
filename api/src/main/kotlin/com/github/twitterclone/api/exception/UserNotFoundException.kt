package com.github.twitterclone.api.exception

class UserNotFoundException(id: String) : Exception("User with username $id not found")