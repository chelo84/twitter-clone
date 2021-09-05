package com.github.twitterclone.client.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class TemporalUtils {
    companion object {
        fun toSystemZone(ldt: LocalDateTime): ZonedDateTime {
            return ldt.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.systemDefault())
        }
    }
}