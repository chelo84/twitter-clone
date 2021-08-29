package com.github.twitterclone.server.mapper

import com.github.twitterclone.sdk.domain.user.NewUser
import com.github.twitterclone.server.model.document.user.User
import com.github.twitterclone.server.repository.user.UserNonReactiveRepository
import org.mapstruct.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import com.github.twitterclone.sdk.domain.user.User as UserSdk

@Component
@Named("UserMapper")
@Mapper(componentModel = "spring")
abstract class UserMapper {
    @Autowired
    private lateinit var userNonReactiveRepository: UserNonReactiveRepository

    @Autowired
    private lateinit var userMapper: UserMapper

    @Mapping(target = "password", ignore = true)
    abstract fun userToDto(user: User): UserSdk

    @UsernameToUserDto
    fun findAndMapUserToUserDto(userId: String) = userMapper.userToDto(
        userNonReactiveRepository.findByUsername(userId)!!
    )

    abstract fun newUserToUser(newUser: NewUser): User

    @BeforeMapping
    fun newUserToUserPasswordValidation(
        newUser: NewUser,
        @MappingTarget user: User
    ) {
        if (newUser.password != newUser.passwordConfirmation)
            throw ResponseStatusException(HttpStatus.CONFLICT, "Password and confirmation do not match")
    }
}

@Qualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class UsernameToUserDto
