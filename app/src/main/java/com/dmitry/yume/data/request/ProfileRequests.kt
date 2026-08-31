package com.dmitry.yume.data.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileRequest(
    val username: String? = null,
    @param:Json(name = "display_name") val displayName: String? = null,
)

@JsonClass(generateAdapter = true)
data class ChangePasswordRequest(
    @param:Json(name = "current_password") val currentPassword: String,
    @param:Json(name = "new_password") val newPassword: String,
)

@JsonClass(generateAdapter = true)
data class ChangeEmailRequest(
    @param:Json(name = "new_email") val newEmail: String,
    val password: String,
)
