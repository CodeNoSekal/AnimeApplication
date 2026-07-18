package com.dmitry.test.animeapplication.data.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusRequest(
    val status: String?
)

@JsonClass(generateAdapter = true)
data class FavoriteRequest(
    val favorite: Boolean
)

@JsonClass(generateAdapter = true)
data class ScoreRequest(
    val score: Int?
)

@JsonClass(generateAdapter = true)
data class ReviewRequest(
    val review: String?
)
