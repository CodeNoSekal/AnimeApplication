package com.dmitry.yume.data.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResolvePlaybackRequest(
    @param:Json(name = "anime_id")
    val animeId: Int,
    @param:Json(name = "episode_number")
    val episodeNumber: Int,
    @param:Json(name = "source_provider")
    val sourceProvider: String,
    @param:Json(name = "voiceover_id")
    val voiceoverId: Int,
)
