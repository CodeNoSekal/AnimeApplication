package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.PlaybackEpisode
import com.dmitry.yume.domain.models.PlaybackCatalog
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.VideoQuality
import com.dmitry.yume.domain.models.PlaybackSource
import com.dmitry.yume.domain.models.Voiceover
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaybackCatalogResponse(
    @param:Json(name = "shikimori_id")
    val animeId: Int,
    val title: String? = null,
    @param:Json(name = "episodes_total")
    val episodesTotal: Int = 0,
    @param:Json(name = "episodes_available")
    val episodesAvailable: Int = 0,
    @param:Json(name = "has_kodik")
    val hasKodik: Boolean = false,
    @param:Json(name = "has_libria")
    val hasLibria: Boolean = false,
    val episodes: List<PlaybackEpisodeResponse> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PlaybackEpisodeResponse(
    @param:Json(name = "episode_number")
    val number: Int,
    val title: String? = null,
    @param:Json(name = "air_date")
    val airDate: String = "",
    @param:Json(name = "is_available")
    val isAvailable: Boolean = true,
    val sources: List<PlaybackSourceResponse> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PlaybackSourceResponse(
    val provider: String = "",
    val voiceovers: List<VoiceoverSummaryResponse> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class VoiceoverSummaryResponse(
    @param:Json(name="voiceover_id")
    val id: Int,
    @param:Json(name = "voiceover")
    val name: String = "",
    @param:Json(name = "quality")
    val maxQuality: String? = "",
    @param:Json(name = "logo_url")
    val logoUrl: String = "",
)

fun PlaybackCatalogResponse.toDomain(): PlaybackCatalog {
    return PlaybackCatalog(
        animeId = animeId,
        title = title,
        episodesTotal = episodesTotal,
        episodesAvailable = episodesAvailable,
        hasKodik = hasKodik,
        hasLibria = hasLibria,
        episodes = episodes.toDomain()
    )
}

@JvmName("episodesToDomain")
fun List<PlaybackEpisodeResponse>.toDomain(): List<PlaybackEpisode> {
    return map {
        PlaybackEpisode(
            number = it.number,
            title = it.title,
            airDate = it.airDate,
            isAvailable = it.isAvailable,
            sources = it.sources.toDomain()
        )
    }
}

@JvmName("sourcesToDomain")
fun List<PlaybackSourceResponse>.toDomain(): List<PlaybackSource> {
    return map {
        PlaybackSource(
            provider = Provider.getProvider(it.provider),
            voiceovers = it.voiceovers.toDomain()
        )
    }
}

@JvmName("voiceoversToDomain")
fun List<VoiceoverSummaryResponse>.toDomain(): List<Voiceover> {
    return map {
        Voiceover(
            id = it.id,
            name = it.name,
            maxQuality = VideoQuality.fromRaw(it.maxQuality),
            logoUrl = it.logoUrl
        )
    }
}
