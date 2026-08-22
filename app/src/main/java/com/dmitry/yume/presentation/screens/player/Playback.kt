package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.PlaybackCatalog
import com.dmitry.yume.domain.models.ProgressItemData
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.VideoQuality

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
        sourceProvider = selectedSourceProvider,
        voiceoverId = selectedVoiceoverId,
        quality = selectedQuality,
        positionMs = currentPositionMs
    )

sealed interface PlaybackResolution {
    data class Success(
        val episodeNumber: Int,
        val sourceProvider: Provider,
        val voiceoverId: Int,
        val voiceover: String,
        val positionMs: Long
    ) : PlaybackResolution

    data class Error(val message: String) : PlaybackResolution
}

fun selectPlayback(
    playbackCatalog: PlaybackCatalog,
    playbackPreference: PlaybackPreference? = null
): PlaybackResolution {
    val episode = playbackCatalog.episodes
        .firstOrNull {
            it.number == playbackPreference?.episodeNumber && it.isAvailable
        }
        ?: playbackCatalog.episodes.firstOrNull { it.isAvailable }
        ?: return PlaybackResolution.Error("Нет доступных эпизодов")

    val source = episode.sources
        .firstOrNull {
            it.provider == playbackPreference?.sourceProvider
        }
        ?: episode.sources.firstOrNull()
        ?: return PlaybackResolution.Error("Нет доступных эпизодов")

    val voiceover = source.voiceovers
        .firstOrNull {
            it.id == playbackPreference?.voiceoverId
        }
        ?: source.voiceovers.firstOrNull()
        ?: return PlaybackResolution.Error("Нет доступных озвучек")

    val positionMs = if (episode.number == playbackPreference?.episodeNumber) {
        playbackPreference.positionMs.coerceAtLeast(0L)
    } else {
        0L
    }

    return PlaybackResolution.Success(
        episodeNumber = episode.number,
        sourceProvider = source.provider,
        voiceoverId = voiceover.id,
        voiceover = voiceover.name,
        positionMs = positionMs
    )
}