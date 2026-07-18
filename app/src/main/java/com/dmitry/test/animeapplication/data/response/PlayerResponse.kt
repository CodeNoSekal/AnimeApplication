package com.dmitry.test.animeapplication.data.response

import com.dmitry.test.animeapplication.domain.models.Episode
import com.dmitry.test.animeapplication.domain.models.PlayerData
import com.dmitry.test.animeapplication.domain.models.Provider
import com.dmitry.test.animeapplication.domain.models.Quality
import com.dmitry.test.animeapplication.domain.models.Source
import com.dmitry.test.animeapplication.domain.models.Voiceover
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayerResponse(
    @param:Json(name = "shikimori_id")
    val id: Int,
    val title: String? = null,
    @param:Json(name = "episodes_total")
    val episodesTotal: Int = 0,
    @param:Json(name = "episodes_available")
    val episodesAvailable: Int = 0,
    @param:Json(name = "has_kodik")
    val kodik: Boolean = false,
    @param:Json(name = "has_anilibria")
    val anilibria: Boolean = false,
    val episodes: List<EpisodeResponse> = emptyList()
)

@JsonClass(generateAdapter = true)
data class EpisodeResponse(
    @param:Json(name = "episode_number")
    val id: Int,
    val title: String? = null,
    @param:Json(name = "air_date")
    val airDate: String = "",
    @param:Json(name = "is_available")
    val isAvailable: Boolean = true,
    val sources: List<SourceResponse> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SourceResponse(
    val provider: String = "",
    val voiceovers: List<VoiceoverResponse> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class VoiceoverResponse(
    @param:Json(name="voiceover_id")
    val voiceoverId: Int,
    val url: String? = null,
    val type: String = "",
    val voiceover: String = "",
    val quality: String = "",
    @param:Json(name = "hls_480")
    val hls480: String? = null,
    @param:Json(name = "hls_720")
    val hls720: String? = null,
    @param:Json(name = "hls_1080")
    val hls1080: String? = null
)

fun PlayerResponse.toDomain(): PlayerData {
    return PlayerData(
        id = id,
        title = title,
        episodesTotal = episodesTotal,
        episodesAvailable = episodesAvailable,
        kodik = kodik,
        anilibria = anilibria,
        episodes = episodes.toDomain()
    )
}

@JvmName("episodesToDomain")
fun List<EpisodeResponse>.toDomain(): List<Episode> {
    return map {
        Episode(
            id = it.id,
            title = it.title,
            airDate = it.airDate,
            isAvailable = it.isAvailable,
            sources = it.sources.toDomain()
        )
    }
}

@JvmName("sourcesToDomain")
fun List<SourceResponse>.toDomain(): List<Source> {
    return map {
        Source(
            provider = Provider.getProvider(it.provider),
            voiceovers = it.voiceovers.toDomain()
        )
    }
}

@JvmName("voiceoversToDomain")
fun List<VoiceoverResponse>.toDomain(): List<Voiceover> {
    return map {
        Voiceover(
            voiceoverId = it.voiceoverId,
            url = it.url,
            type = it.type,
            voiceover = it.voiceover,
            quality = Quality.getQuality(it.quality),
            hls480 = it.hls480,
            hls720 = it.hls720,
            hls1080 = it.hls1080
        )
    }
}
