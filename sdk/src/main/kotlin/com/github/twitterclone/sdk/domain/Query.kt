package com.github.twitterclone.sdk.domain

import javax.validation.constraints.Max
import javax.validation.constraints.Min

abstract class Query(
    @field:Min(0)
    val page: Int,
    @field:Min(1)
    @field:Max(20)
    var size: Int,
)