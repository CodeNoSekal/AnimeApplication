package com.dmitry.test.animeapplication.data.response

import com.dmitry.test.animeapplication.domain.models.ProgressData
import com.dmitry.test.animeapplication.domain.models.ProgressItemData
import com.dmitry.test.animeapplication.domain.models.Provider
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ProgressResponse(
    val items: List<ProgressItem>
)

@JsonClass(generateAdapter = true)
data class ProgressItem(
    @param:Json(name = "shikimori_id")
    val id: Int,
    val title: String?,
    @param:Json(name = "poster_thumb")
    val posterUrl: String?,
    @param:Json(name = "episode_number")
    val episodeNumber: Int,
    @param:Json(name = "position_ms")
    val positionMs: Long,
    @param:Json(name = "duration_ms")
    val durationMs: Long,
    @param:Json(name = "is_completed")
    val isCompleted: Boolean,
    @param:Json(name = "source_provider")
    val sourceProvider: String,
    @param:Json(name = "voiceover_id")
    val voiceoverId: Int,
)

fun ProgressResponse.toDomain(): ProgressData =
    ProgressData(
        items.toDomain()
    )

@JvmName("progressToDomain")
fun List<ProgressItem>.toDomain(): List<ProgressItemData> {
    val result = mutableListOf<ProgressItemData>()

    this.forEach {
        result.add(
            it.toDomain()
        )
    }

    return result
}

fun ProgressItem.toDomain(): ProgressItemData =
    ProgressItemData(
        id,
        title,
        posterUrl,
        episodeNumber,
        positionMs,
        durationMs,
        isCompleted,
        Provider.getProvider(sourceProvider),
        voiceoverId
    )

