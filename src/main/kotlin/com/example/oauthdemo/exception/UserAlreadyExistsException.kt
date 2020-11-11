package com.example.oauthdemo.exception

class UserAlreadyExistsException(username: String) : Exception("User $username already exists.")