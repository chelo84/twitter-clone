package com.github.twitterclone.server.repository.user

import com.github.twitterclone.server.model.document.user.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserNonReactiveRepository : MongoRepository<User, String> {
    fun findByUsername(sub: String): User?
}