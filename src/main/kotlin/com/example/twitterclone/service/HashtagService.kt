package com.example.twitterclone.service

import com.example.twitterclone.model.document.Hashtag
import com.example.twitterclone.repository.tweet.HashtagRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class HashtagService(private val hashtagRepository: HashtagRepository) {
    // Search for words starting with '#' and ending with: an whitespace or in the end of the string
    private final val hashtagRegex = Regex("\\B\\#\\w+(?=\\s|$)")

    fun getHashtags(text: String): Flux<Hashtag> {
        val textHashtags = hashtagsValuesFromText(text)

        return Flux
                .fromIterable(textHashtags)
                .flatMap { hashtagValue ->
                    hashtagRepository.findByHashtag(hashtagValue.trim())
                            .switchIfEmpty(createHashtag(hashtagValue.trim()))
                }
    }

    private fun hashtagsValuesFromText(text: String): List<String> = hashtagRegex.findAll(text)
            .map { it.value }
            .toList()


    private fun createHashtag(hashtag: String): Mono<Hashtag> {
        return hashtagRepository.save(Hashtag(hashtag))
    }
}