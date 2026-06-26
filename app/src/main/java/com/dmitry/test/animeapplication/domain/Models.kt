package com.dmitry.test.animeapplication.domain

import com.squareup.moshi.Json

data class Anime(
    val id: Int,
    val title: String?,
    val titleEn: String?,
    val posterUrl: String?,
    val year: Int?,
    val rating: Double?
)

data class AnimeDetailed(
    val id: Int,
    val title: String?,
    val titleEn: String?,
    val posterUrl: String?,
    val year: Int?,
    val rating: Double?
)

data class User(
    val id: Int,
    val email: String,
    val emailVerified: Boolean,
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
    val isAdmin: Boolean
)
