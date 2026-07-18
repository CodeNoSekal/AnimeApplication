package com.dmitry.test.animeapplication.presentation.screens.player

import com.dmitry.test.animeapplication.domain.models.PlayerData
import com.dmitry.test.animeapplication.domain.models.ProgressItemData
import com.dmitry.test.animeapplication.domain.models.Provider
import com.dmitry.test.animeapplication.domain.models.Quality
import com.dmitry.test.animeapplication.domain.models.getAvailableEpisode
import com.dmitry.test.animeapplication.domain.models.hlsByQuality

data class PreferredPlayback(
    val episodeNumber: Int,
    val sourceProvider: Provider?,
    val voiceoverId: Int?,
    val positionMs: Long
)

fun ProgressItemData.toPreferredPlayback(): PreferredPlayback =
    PreferredPlayback(
        episodeNumber,
        sourceProvider,
        voiceoverId,
        positionMs
    )

data class ResolvedPlayback(
    val episodeNumber: Int,
    val sourceProvider: Provider,
    val voiceoverId: Int,
    val quality: Quality,
    val url: String?,
    val positionMs: Long
)

fun resolveInitialState(
    playerData: PlayerData,
    preferredPlayback: PreferredPlayback? = null
): ResolvedPlayback {
    val episode =
        playerData.episodes.find { it.id == preferredPlayback?.episodeNumber && it.isAvailable }
            ?: playerData.getAvailableEpisode()!!

    val provider =
        episode.sources.find { it.provider == preferredPlayback?.sourceProvider }
            ?: episode.sources.first()

    val voiceover =
        provider.voiceovers.find { it.voiceoverId == preferredPlayback?.voiceoverId }
            ?: provider.voiceovers.first()

    val positionMs = if (episode.id == preferredPlayback?.episodeNumber) {
        preferredPlayback.positionMs
    } else {
        0
    }

    val quality = voiceover.quality

    val episodeUrl = voiceover.hlsByQuality(quality)

    return ResolvedPlayback(
        episodeNumber = episode.id,
        sourceProvider = provider.provider,
        voiceoverId = voiceover.voiceoverId,
        quality = quality,
        url = episodeUrl,
        positionMs = positionMs
    )
}