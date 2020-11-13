package com.example.oauthdemo.config

import com.example.oauthdemo.security.auditor.SpringSecurityAuditorAware
import com.example.oauthdemo.security.auditor.SpringSecurityReactiveAuditorAware
import com.mongodb.reactivestreams.client.MongoClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean


@Configuration
@EnableMongoAuditing
@EnableReactiveMongoAuditing
@ConfigurationProperties(prefix = "spring.data.mongodb")
@EnableMongoRepositories(basePackages = arrayOf("com.example.oauthdemo.repository"))
@EnableReactiveMongoRepositories(basePackages = arrayOf("com.example.oauthdemo.repository"))
class MongoConfig : AbstractMongoClientConfiguration() {
    lateinit var database: String

    override fun getDatabaseName(): String = database

    @Bean
    fun mongoTemplate(mongoClient: com.mongodb.client.MongoClient) = MongoTemplate(mongoClient, this.databaseName)

    @Bean
    fun reactiveMongoTemplate(mongoClient: MongoClient) = ReactiveMongoTemplate(mongoClient, this.databaseName)

    @Bean
    fun auditorAware() = SpringSecurityAuditorAware()

    @Bean
    fun reactiveAuditorAware() = SpringSecurityReactiveAuditorAware()

    @Bean
    fun localValidatorFactoryBean() = LocalValidatorFactoryBean()

    @Bean
    fun validatingMongoEventListener(lfb: LocalValidatorFactoryBean?) = ValidatingMongoEventListener(lfb!!)
}