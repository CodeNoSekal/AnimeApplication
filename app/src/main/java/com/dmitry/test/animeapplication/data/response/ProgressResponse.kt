package com.dmitry.test.animeapplication.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ProgressResponse(
    @param:Json(name = "shikimori_id")
    val id: Int,
    val title: String?,
    @param:Json(name = "poster_thumb")
    val posterUrl: String?,
    @param:Json(name = "episode_number")
    val episodeNumber: Int,
    @param:Json(name = "position_sec")
    val positionSec: Int,
    @param:Json(name = "duration_sec")
    val durationSec: Int,
    @param:Json(name = "is_completed")
    val isCompleted: Boolean,
    @param:Json(name = "source_provider")
    val sourceProvider: String,
    @param:Json(name = "voiceover_id")
    val voiceoverId: Int,
)
