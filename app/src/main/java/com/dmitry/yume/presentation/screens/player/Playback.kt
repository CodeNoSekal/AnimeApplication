package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.PlayerData
import com.dmitry.yume.domain.models.ProgressItemData
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.Quality
import com.dmitry.yume.domain.models.Source
import com.dmitry.yume.domain.models.Voiceover

data class PreferredPlayback(
    val episodeNumber: Int?,
    val sourceProvider: Provider?,
    val voiceoverId: Int?,
    val quality: Quality?,
    val positionMs: Long
)

fun ProgressItemData.toPreferredPlayback(): PreferredPlayback =
    PreferredPlayback(
        episodeNumber = episodeNumber,
        sourceProvider = sourceProvider,
        voiceoverId = voiceoverId,
        quality = null,
        positionMs = positionMs
    )

fun PlayerUiState.toPreferredPlayback(): PreferredPlayback =
    PreferredPlayback(
        episodeNumber = selectedEpisodeNumber,
        sourceProvider = selectedSource,
        voiceoverId = selectedVoiceoverId,
        quality = selectedQuality,
        positionMs = currentPositionMs
    )

sealed interface PlaybackResolution {
    data class Success(
        val episodeNumber: Int,
        val sourceProvider: Provider,
        val voiceoverId: Int,
        val quality: Quality,
        val url: String,
        val positionMs: Long
    ) : PlaybackResolution

    data class Error(val message: String) : PlaybackResolution
}

fun resolvePlayback(
    playerData: PlayerData,
    preferredPlayback: PreferredPlayback? = null
): PlaybackResolution {
    val episode = playerData.episodes
        .firstOrNull {
            it.id == preferredPlayback?.episodeNumber && it.isAvailable
        }
        ?: playerData.episodes.firstOrNull { it.isAvailable }
        ?: return PlaybackResolution.Error("Нет доступных эпизодов")

    if (episode.sources.isEmpty()) {
        return PlaybackResolution.Error("Для серии нет доступных источников")
    }

    val sourceCandidates = episode.sources.preferredFirst {
        it.provider == preferredPlayback?.sourceProvider
    }

    val hasVoiceovers = sourceCandidates.any { it.voiceovers.isNotEmpty() }

    for (source in sourceCandidates) {
        val voiceoverCandidates = source.voiceovers.preferredFirst {
            it.voiceoverId == preferredPlayback?.voiceoverId
        }

        for (voiceover in voiceoverCandidates) {
            val stream = voiceover.resolveStream(preferredPlayback?.quality)
                ?: continue

            val positionMs = if (episode.id == preferredPlayback?.episodeNumber) {
                preferredPlayback.positionMs.coerceAtLeast(0L)
            } else {
                0L
            }

            return PlaybackResolution.Success(
                episodeNumber = episode.id,
                sourceProvider = source.provider,
                voiceoverId = voiceover.voiceoverId,
                quality = stream.quality,
                url = stream.url,
                positionMs = positionMs
            )
        }
    }

    return if (hasVoiceovers) {
        PlaybackResolution.Error("Для серии отсутствует ссылка на видео")
    } else {
        PlaybackResolution.Error("Для серии нет доступных озвучек")
    }
}

private data class ResolvedStream(
    val quality: Quality,
    val url: String
)

private fun Voiceover.resolveStream(
    preferredQuality: Quality?
): ResolvedStream? {
    val qualityCandidates = when (preferredQuality) {
        Quality.FHD -> listOf(Quality.FHD, Quality.HD, Quality.SD)
        Quality.HD -> listOf(Quality.HD, Quality.SD, Quality.FHD)
        Quality.SD -> listOf(Quality.SD, Quality.HD, Quality.FHD)
        Quality.Undefined, null -> listOf(Quality.FHD, Quality.HD, Quality.SD)
    }

    for (candidate in qualityCandidates) {
        val candidateUrl = urlFor(candidate)
            ?.takeIf { it.isNotBlank() }
            ?: continue

        return ResolvedStream(candidate, candidateUrl)
    }

    val fallbackUrl = url?.takeIf { it.isNotBlank() }
        ?: return null

    return ResolvedStream(
        quality = quality.takeUnless { it == Quality.Undefined }
            ?: Quality.Undefined,
        url = fallbackUrl
    )
}

private fun Voiceover.urlFor(quality: Quality): String? =
    when (quality) {
        Quality.FHD -> hls1080
        Quality.HD -> hls720
        Quality.SD -> hls480
        Quality.Undefined -> null
    }

private fun <T> List<T>.preferredFirst(
    predicate: (T) -> Boolean
): List<T> {
    val preferred = firstOrNull(predicate) ?: return this
    return listOf(preferred) + filterNot { it === preferred }
}
