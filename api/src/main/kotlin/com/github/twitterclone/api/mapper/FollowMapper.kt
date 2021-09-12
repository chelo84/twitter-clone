package com.github.twitterclone.api.mapper

import com.github.twitterclone.api.model.document.follow.Follow
import org.mapstruct.Mapper
import org.springframework.stereotype.Component
import com.github.twitterclone.sdk.domain.follow.Follow as FollowSdk

@Component
@Mapper(componentModel = "spring")
abstract class FollowMapper {

    abstract fun followToDto(follow: Follow): FollowSdk

    abstract fun dtoToFollow(followDto: FollowSdk): Follow
}