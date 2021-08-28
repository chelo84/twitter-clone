package com.github.twitterclone.server.security.jwt

import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.KeyLengthException
import com.nimbusds.jose.crypto.MACSigner

class JWTCustomSigner {
    val signer: JWSSigner? = try {
        MACSigner(JWTSecrets.DEFAULT_SECRET)
    } catch (e: KeyLengthException) {
        null
    }
}