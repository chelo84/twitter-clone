package com.example.oauthdemo.repository.user

import com.example.oauthdemo.model.document.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserNonReactiveRepository : MongoRepository<User, String> {
    fun findByUsername(sub: String, email: String): User?
}