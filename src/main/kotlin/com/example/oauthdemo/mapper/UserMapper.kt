package com.example.oauthdemo.mapper

import com.example.oauthdemo.model.document.User
import com.example.oauthdemo.model.dto.UserDto
import com.example.oauthdemo.repository.user.UserNonReactiveRepository
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.Qualifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
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

    @UserIdToUserDto
    fun findAndMapUserToUserDto(userId: String) = userMapper.userToDto(
            userNonReactiveRepository.findByIdOrNull(userId)!!
    )

    abstract fun dtoToUser(userDto: UserDto): User
}

@Qualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class UserIdToUserDto