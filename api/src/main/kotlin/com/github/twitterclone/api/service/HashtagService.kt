package com.github.twitterclone.api.service

import com.github.twitterclone.api.model.document.Hashtag
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Service
class HashtagService {
    // Search for words starting with '#' and ending with: an whitespace or in the end of the string
    private val hashtagRegex = Regex("\\B#\\w+(?=\\s|$)")

    fun getHashtags(text: String): Flux<Hashtag> {
        return Flux.fromIterable(hashtagsValuesFromText(text))
    }

    private fun hashtagsValuesFromText(text: String): List<Hashtag> {
        val seen: ConcurrentMap<String, Boolean> = ConcurrentHashMap()
        val hashtags = hashtagRegex.findAll(text)
            .map {
                Hashtag(
                    hashtag = it.value,
                    startsAt = it.range.first,
                    endsAt = it.range.last
                )
            }
            .filter { hashtag ->
                val isSeen = seen[hashtag.hashtag] ?: false
                if (isSeen) {
                    throw HashtagAppearsTwiceException(hashtag)
                }
                return@filter true.also { seen[hashtag.hashtag] = true }
            }
            .toList()

        return hashtags
    }

    private class HashtagAppearsTwiceException(hashtag: Hashtag) :
        Exception("Cannot have the same hashtag twice! Hashtag: ${hashtag.hashtag}")
}