package com.dmitry.test.animeapplication.domain.models

data class User(
    val id: Int,
    val email: String,
    val emailVerified: Boolean,
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
    val isAdmin: Boolean
)
