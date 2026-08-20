package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.VideoQuality

data class PlaybackUiState(
    val selectedEpisodeNumber: Int = 1,
    val selectedSource: Provider = Provider.Libria,
    val selectedVoiceoverId: Int? = null,
    val selectedQuality: VideoQuality = VideoQuality.FHD,
    val currentUrl: String? = null,
    val currentPositionMs: Long = 0L
)

fun PlaybackResolution.Success.toPlaybackUiState(): PlaybackUiState =
    PlaybackUiState(
        selectedEpisodeNumber = episodeNumber,
        selectedSource = sourceProvider,
        selectedVoiceoverId = voiceoverId,
        selectedQuality = quality,
        currentUrl = url,
        currentPositionMs = positionMs
    )
