package com.example.oauthdemo.security.jwt

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.SignedJWT
import reactor.core.publisher.Mono
import java.text.ParseException
import java.time.Instant
import java.util.*

class JWTCustomVerifier {
    private val jwsVerifier = buildJWSVerifier()

    fun check(token: String): Mono<SignedJWT> {
        return Mono.justOrEmpty(createJWS(token))
                .filter(::isNotExpired)
                .filter(::validSignature)
    }

    private fun isNotExpired(token: SignedJWT): Boolean = getExpirationDate(token)!!.after(Date.from(Instant.now()))

    private fun validSignature(token: SignedJWT): Boolean {
        return try {
            token.verify(jwsVerifier)
        } catch (e: JOSEException) {
            e.printStackTrace()
            false
        }
    }

    private fun buildJWSVerifier(): JWSVerifier? {
        return try {
            MACVerifier(JWTSecrets.DEFAULT_SECRET)
        } catch (e: JOSEException) {
            e.printStackTrace()
            null
        }
    }

    private fun createJWS(token: String): SignedJWT? {
        return try {
            SignedJWT.parse(token)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    private fun getExpirationDate(token: SignedJWT): Date? {
        return try {
            token.jwtClaimsSet.expirationTime
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }
}