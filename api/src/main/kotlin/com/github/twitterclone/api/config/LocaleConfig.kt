package com.github.twitterclone.api.config

import org.springframework.context.annotation.Configuration
import java.util.*
import javax.annotation.PostConstruct


@Configuration
class LocaleConfig {

    @PostConstruct
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }
}