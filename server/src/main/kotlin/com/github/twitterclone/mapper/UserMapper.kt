package com.github.twitterclone.mapper

import com.github.twitterclone.model.document.user.User
import com.github.twitterclone.model.dto.user.UserDto
import com.github.twitterclone.repository.user.UserNonReactiveRepository
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.Qualifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Named("UserMapper")
@Mapper(componentModel = "spring")
abstract class UserMapper {
    @Autowired
    private lateinit var userNonReactiveRepository: UserNonReactiveRepository

    @Autowired
    private lateinit var userMapper: UserMapper

    @Mapping(target = "password", ignore = true)
    abstract fun userToDto(user: User): UserDto

    @UsernameToUserDto
    fun findAndMapUserToUserDto(userId: String) = userMapper.userToDto(
        userNonReactiveRepository.findByUsername(userId)!!
    )

    abstract fun dtoToUser(userDto: UserDto): User
}

@Qualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class UsernameToUserDto