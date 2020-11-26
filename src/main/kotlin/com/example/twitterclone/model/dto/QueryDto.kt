package com.example.twitterclone.model.dto

abstract class QueryDto(
        val page: Int,
        var size: Int,
) : QueryValidated {

    /**
     * If a message is returned, it's because an attribute is not valid
     */
    @Throws(QueryValidationException::class)
    fun validateQuery() {
        validate()?.apply {
            throw QueryValidationException(this)
        }
    }

    override fun validate(): String? {
        return when {
            size > 20 -> "Size cannot be higher than 20"
            page < 0 -> "Page cannot be lesser than 0"
            else -> null
        }
    }
}

interface QueryValidated {
    fun validate(): String?
}

class QueryValidationException(message: String) : Exception(message)