package com.dmitry.yume.data.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefreshRequest(
    @param:Json(name = "refresh_token")
    val refreshToken: String
)