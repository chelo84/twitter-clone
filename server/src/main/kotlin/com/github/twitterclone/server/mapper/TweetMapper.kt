package com.github.twitterclone.server.mapper

import com.github.twitterclone.sdk.domain.tweet.NewTweet
import com.github.twitterclone.server.model.document.Tweet
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.springframework.stereotype.Component
import com.github.twitterclone.sdk.domain.tweet.Tweet as TweetSdk

@Component
@Mapper(componentModel = "spring", uses = [UserMapper::class])
abstract class TweetMapper {
    @Mappings(
        Mapping(target = "user", qualifiedBy = [UsernameToUserDto::class])
    )
    abstract fun tweetToDto(tweet: Tweet): TweetSdk

    @Mappings(
        Mapping(target = "uid", ignore = true),
        Mapping(target = "user", ignore = true),
        Mapping(target = "createdDate", ignore = true),
        Mapping(target = "lastModifiedDate", ignore = true),
        Mapping(target = "hashtags", ignore = true)
    )
    abstract fun newTweetDtoToTweet(tweetDto: NewTweet): Tweet

    @Mappings(
        Mapping(source = "user.id", target = "user")
    )
    abstract fun dtoToTweet(tweetDto: TweetSdk): Tweet
}
