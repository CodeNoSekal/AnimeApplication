package com.dmitry.yume.data.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyRequest(
    val code: String
)