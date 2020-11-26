package com.example.twitterclone.security.auditor

import com.example.twitterclone.model.document.user.User
import org.springframework.data.domain.AuditorAware
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import reactor.core.publisher.Mono
import java.util.*

class SpringSecurityAuditorAware : AuditorAware<String> {

    override fun getCurrentAuditor(): Optional<String> {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter { obj: Authentication -> obj.isAuthenticated }
                .map { obj: Authentication -> obj.principal as User }
                .map { it.username }
    }
}

class SpringSecurityReactiveAuditorAware : ReactiveAuditorAware<String> {

    override fun getCurrentAuditor(): Mono<String> {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter { obj: Authentication -> obj.isAuthenticated }
                .map { obj: Authentication -> obj.principal as User }
                .flatMap { Mono.just(it.username) }
    }
}