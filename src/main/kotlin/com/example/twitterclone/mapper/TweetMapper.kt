package com.example.twitterclone.mapper

import com.example.twitterclone.model.document.Tweet
import com.example.twitterclone.model.dto.PostDto
import com.example.twitterclone.model.dto.TweetDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.springframework.stereotype.Component

@Component
@Mapper(componentModel = "spring", uses = [UserMapper::class])
abstract class TweetMapper {
    @Mappings(
            Mapping(target = "createdBy", qualifiedBy = [UserIdToUserDto::class])
    )
    abstract fun tweetToDto(tweet: Tweet): TweetDto

    @Mappings(
            Mapping(target = "createdBy", ignore = true),
            Mapping(target = "createdDate", ignore = true),
            Mapping(target = "hashtags", ignore = true)
    )
    abstract fun newTweetDtoToTweet(tweetDto: TweetDto): Tweet

    @Mappings(
            Mapping(source = "createdBy.id", target = "createdBy")
    )
    abstract fun dtoToTweet(tweetDto: TweetDto): Tweet
}
