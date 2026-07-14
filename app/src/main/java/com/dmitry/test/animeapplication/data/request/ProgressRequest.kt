package com.dmitry.test.animeapplication.data.request

import com.squareup.moshi.Json

data class ProgressRequest(
    @param:Json(name = "anime_id")
    val animeId: Int,
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
