package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.Quality

data class PlayerUiState(
    val selectedEpisodeNumber: Int = 1,
    val selectedSource: Provider = Provider.Libria,
    val selectedVoiceoverId: Int? = null,
    val selectedQuality: Quality = Quality.FHD,
    val currentUrl: String? = null,
    val currentPositionMs: Long = 0L
)

fun PlaybackResolution.Success.toPlayerUiState(): PlayerUiState =
    PlayerUiState(
        selectedEpisodeNumber = episodeNumber,
        selectedSource = sourceProvider,
        selectedVoiceoverId = voiceoverId,
        selectedQuality = quality,
        currentUrl = url,
        currentPositionMs = positionMs
    )
