package com.dmitry.test.animeapplication.data.response

import com.dmitry.test.animeapplication.domain.models.Status
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusResponse(
    @param:Json(name = "anime_id")
    val animeId: Int,
    val status: String?,
    val favorite: Boolean,
    val score: Int?,
    val review: String?
)

fun StatusResponse.toDomain(): Status =
    Status(
        animeId,
        status,
        favorite,
        score,
        review
    )
