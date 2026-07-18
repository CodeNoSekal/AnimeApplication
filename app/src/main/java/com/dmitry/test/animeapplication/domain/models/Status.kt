package com.dmitry.test.animeapplication.domain.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Status(
    val animeId: Int,
    val status: String?,
    val favorite: Boolean,
    val score: Int?,
    val review: String?
)