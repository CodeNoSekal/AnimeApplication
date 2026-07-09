package com.dmitry.test.animeapplication.presentation.screens.player

import com.dmitry.test.animeapplication.domain.models.Player
import com.dmitry.test.animeapplication.domain.models.Provider
import com.dmitry.test.animeapplication.domain.models.Quality
import com.dmitry.test.animeapplication.domain.models.getEpisode
import com.dmitry.test.animeapplication.domain.models.getSource
import com.dmitry.test.animeapplication.domain.models.getVoiceover
import com.dmitry.test.animeapplication.domain.models.hlsByQuality

data class PlayerUiState(
    val selectedEpisodeNumber: Int = 1,
    val selectedSource: Provider = Provider.Anilibria,
    val selectedVoiceover: String = "",
    val selectedQuality: Quality = Quality.FHD,
    val currentUrl: String? = null,
    val currentPositionMs: Long = 0L
)

fun updateState(
    data: Player,
    target: PlayerUiState
): PlayerUiState {
    val targetEpisode = data.getEpisode(target.selectedEpisodeNumber)

    val targetSource = targetEpisode.getSource(target.selectedSource)

    val targetVoiceover = targetSource.getVoiceover(target.selectedVoiceover)

    return PlayerUiState(
        selectedEpisodeNumber = targetEpisode.id,
        selectedSource = targetSource.provider,
        selectedVoiceover = targetVoiceover.voiceover,
        selectedQuality = target.selectedQuality,
        currentUrl = targetVoiceover.hlsByQuality(target.selectedQuality)
    )
}