package com.example.oauthdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class OauthDemoApplication

fun main(args: Array<String>) {
    runApplication<OauthDemoApplication>(*args)
}