package com.example.twitterclone.exception

class UserNotFoundException(id: String) : Exception("User with ID $id not found")