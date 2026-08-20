package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.PlaybackCatalog
import com.dmitry.yume.domain.models.ProgressItemData
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.VideoQuality
import com.dmitry.yume.domain.models.Voiceover

data class PlaybackPreference(
    val episodeNumber: Int?,
    val sourceProvider: Provider?,
    val voiceoverId: Int?,
    val quality: VideoQuality?,
    val positionMs: Long
)

fun ProgressItemData.toPlaybackPreference(): PlaybackPreference =
    PlaybackPreference(
        episodeNumber = episodeNumber,
        sourceProvider = sourceProvider,
        voiceoverId = voiceoverId,
        quality = null,
        positionMs = positionMs
    )

fun PlaybackUiState.toPlaybackPreference(): PlaybackPreference =
    PlaybackPreference(
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
        val quality: VideoQuality,
        val url: String,
        val positionMs: Long
    ) : PlaybackResolution

    data class Error(val message: String) : PlaybackResolution
}

fun resolvePlayback(
    playbackCatalog: PlaybackCatalog,
    playbackPreference: PlaybackPreference? = null
): PlaybackResolution {
    val episode = playbackCatalog.episodes
        .firstOrNull {
            it.number == playbackPreference?.episodeNumber && it.isAvailable
        }
        ?: playbackCatalog.episodes.firstOrNull { it.isAvailable }
        ?: return PlaybackResolution.Error("Нет доступных эпизодов")

    if (episode.sources.isEmpty()) {
        return PlaybackResolution.Error("Для серии нет доступных источников")
    }

    val sourceCandidates = episode.sources.preferredFirst {
        it.provider == playbackPreference?.sourceProvider
    }

    val hasVoiceovers = sourceCandidates.any { it.voiceovers.isNotEmpty() }

    for (source in sourceCandidates) {
        val voiceoverCandidates = source.voiceovers.preferredFirst {
            it.id == playbackPreference?.voiceoverId
        }

        for (voiceover in voiceoverCandidates) {
            val stream = voiceover.resolveStream(playbackPreference?.quality)
                ?: continue

            val positionMs = if (episode.number == playbackPreference?.episodeNumber) {
                playbackPreference.positionMs.coerceAtLeast(0L)
            } else {
                0L
            }

            return PlaybackResolution.Success(
                episodeNumber = episode.number,
                sourceProvider = source.provider,
                voiceoverId = voiceover.id,
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
    val quality: VideoQuality,
    val url: String
)

private fun Voiceover.resolveStream(
    preferredQuality: VideoQuality?
): ResolvedStream? {
    val qualityCandidates = when (preferredQuality) {
        VideoQuality.FHD -> listOf(VideoQuality.FHD, VideoQuality.HD, VideoQuality.SD)
        VideoQuality.HD -> listOf(VideoQuality.HD, VideoQuality.SD, VideoQuality.FHD)
        VideoQuality.SD -> listOf(VideoQuality.SD, VideoQuality.HD, VideoQuality.FHD)
        VideoQuality.Unknown, null -> listOf(VideoQuality.FHD, VideoQuality.HD, VideoQuality.SD)
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
        quality = maxQuality.takeUnless { it == VideoQuality.Unknown }
            ?: VideoQuality.Unknown,
        url = fallbackUrl
    )
}

private fun Voiceover.urlFor(quality: VideoQuality): String? =
    when (quality) {
        VideoQuality.FHD -> hls1080
        VideoQuality.HD -> hls720
        VideoQuality.SD -> hls480
        VideoQuality.Unknown -> null
    }

private fun <T> List<T>.preferredFirst(
    predicate: (T) -> Boolean
): List<T> {
    val preferred = firstOrNull(predicate) ?: return this
    return listOf(preferred) + filterNot { it === preferred }
}
