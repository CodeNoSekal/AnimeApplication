package com.dmitry.test.animeapplication.data.response

import com.dmitry.test.animeapplication.domain.models.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @param:Json(name = "access_token")
    val accessToken: String,
    @param:Json(name = "refresh_token")
    val refreshToken: String,
    val user: UserDTO?
)

@JsonClass(generateAdapter = true)
data class VerificationResponse(
    val status: String
)

@JsonClass(generateAdapter = true)
data class UserDTO(
    val id: Int,
    val email: String,
    @param:Json(name = "email_verified")
    val emailVerified: Boolean,
    val username: String,
    @param:Json(name = "display_name")
    val displayName: String,
    @param:Json(name = "avatar_url")
    val avatarUrl: String?,
    @param:Json(name = "is_admin")
    val isAdmin: Boolean
)

fun UserDTO.toDomain(): User {
    return User(
        id,
        email,
        emailVerified,
        username,
        displayName,
        avatarUrl,
        isAdmin
    )
}
