package com.example.oauthdemo.exception

class UserNotFoundException(id: String) : Exception("User with ID $id not found")