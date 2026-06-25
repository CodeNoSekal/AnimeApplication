package com.dmitry.test.animeapplication.data.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyRequest(
    val code: String
)