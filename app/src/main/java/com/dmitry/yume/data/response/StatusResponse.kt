package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.Status
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusResponse(
    @param:Json(name = "anime_id")
    val animeId: Int,
    val status: String? = null,
    val favorite: Boolean = false,
    val score: Int? = null,
    val review: String? = null
)

fun StatusResponse.toDomain(): Status =
    Status(
        animeId,
        status,
        favorite,
        score,
        review
    )
