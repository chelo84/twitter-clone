package com.example.twitterclone.repository.user

import com.example.twitterclone.model.document.user.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserNonReactiveRepository : MongoRepository<User, String> {
    fun findByUsername(sub: String): User?
}