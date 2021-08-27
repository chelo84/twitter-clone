package com.github.twitterclone.mapper

import com.github.twitterclone.model.document.Tweet
import com.github.twitterclone.model.dto.tweet.TweetDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.springframework.stereotype.Component

@Component
@Mapper(componentModel = "spring", uses = [UserMapper::class])
abstract class TweetMapper {
    @Mappings(
        Mapping(target = "user", qualifiedBy = [UsernameToUserDto::class])
    )
    abstract fun tweetToDto(tweet: Tweet): TweetDto

    @Mappings(
        Mapping(target = "user", ignore = true),
        Mapping(target = "createdDate", ignore = true),
        Mapping(target = "lastModifiedDate", ignore = true),
        Mapping(target = "hashtags", ignore = true)
    )
    abstract fun newTweetDtoToTweet(tweetDto: TweetDto): Tweet

    @Mappings(
        Mapping(source = "user.id", target = "user")
    )
    abstract fun dtoToTweet(tweetDto: TweetDto): Tweet
}
