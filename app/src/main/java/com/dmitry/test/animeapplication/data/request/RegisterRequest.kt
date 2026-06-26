package com.dmitry.test.animeapplication.data.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val email: String,
    val password: String,
    @param:Json(name = "display_name")
    val displayName: String
)