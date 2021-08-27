package com.github.twitterclone.exception

class UserNotFoundException(id: String) : Exception("User with username $id not found")