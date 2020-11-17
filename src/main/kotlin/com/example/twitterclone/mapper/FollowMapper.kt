package com.example.twitterclone.mapper

import com.example.twitterclone.model.document.follow.Follow
import com.example.twitterclone.model.dto.follow.FollowDto
import org.mapstruct.Mapper
import org.springframework.stereotype.Component

@Component
@Mapper(componentModel = "spring")
abstract class FollowMapper {

    abstract fun followToDto(follow: Follow): FollowDto

    abstract fun dtoToFollow(followDto: FollowDto): Follow
}