package com.dmitry.test.animeapplication.data.request

import com.dmitry.test.animeapplication.domain.models.Progress
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProgressRequest(
    @param:Json(name = "anime_id")
    val animeId: Int,
    @param:Json(name = "episode_number")
    val episodeNumber: Int,
    @param:Json(name = "position_ms")
    val positionMs: Long,
    @param:Json(name = "duration_ms")
    val durationMs: Long,
    @param:Json(name = "source_provider")
    val sourceProvider: String?,
    @param:Json(name = "voiceover_id")
    val voiceoverId: Int?,
){
    companion object {
        fun from(progress: Progress): ProgressRequest =
            ProgressRequest(
                progress.animeId,
                progress.episodeNumber,
                progress.positionMs,
                progress.durationMs,
                progress.sourceProvider,
                progress.voiceoverId
            )
    }
}

