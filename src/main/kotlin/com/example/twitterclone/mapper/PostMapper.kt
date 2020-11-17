package com.example.twitterclone.mapper

import com.example.twitterclone.model.document.Post
import com.example.twitterclone.model.dto.PostDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.springframework.stereotype.Component

@Component
@Mapper(componentModel = "spring", uses = [UserMapper::class])
abstract class PostMapper {
    @Mappings(
            Mapping(target = "createdBy", qualifiedBy = [UserIdToUserDto::class]),
            Mapping(target = "lastModifiedBy", qualifiedBy = [UserIdToUserDto::class])
    )
    abstract fun postToDto(post: Post): PostDto

    @Mappings(
            Mapping(source = "createdBy.id", target = "createdBy"),
            Mapping(source = "lastModifiedBy.id", target = "lastModifiedBy")
    )
    abstract fun dtoToPost(postDto: PostDto): Post
}
