package com.dmitry.yume.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsernameAvailabilityResponse(
    val username: String,
    val available: Boolean,
)

@JsonClass(generateAdapter = true)
data class UsernameSuggestionResponse(
    @param:Json(name = "display_name") val displayName: String,
    val username: String,
)

@JsonClass(generateAdapter = true)
data class AvatarResponse(
    val status: String,
    @param:Json(name = "avatar_url") val avatarUrl: String,
)

@JsonClass(generateAdapter = true)
data class EmailChangeResponse(
    val status: String,
    val email: String,
    @param:Json(name = "email_verified") val emailVerified: Boolean = false,
)
