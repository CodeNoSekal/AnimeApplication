package com.dmitry.test.animeapplication.presentation.screens.player

import com.dmitry.test.animeapplication.domain.models.PlayerData
import com.dmitry.test.animeapplication.domain.models.Progress
import com.dmitry.test.animeapplication.domain.models.Provider
import com.dmitry.test.animeapplication.domain.models.Quality
import com.dmitry.test.animeapplication.domain.models.getEpisode
import com.dmitry.test.animeapplication.domain.models.getSource
import com.dmitry.test.animeapplication.domain.models.getVoiceover
import com.dmitry.test.animeapplication.domain.models.hlsByQuality

data class PlayerUiState(
    val selectedEpisodeNumber: Int = 1,
    val selectedSource: Provider = Provider.Libria,
    val selectedVoiceoverId: Int? = null,
    val selectedQuality: Quality = Quality.FHD,
    val currentUrl: String? = null,
    val currentPositionMs: Long = 0L
)

fun PlayerUiState.toProgress(animeId: Int, positionMs: Long, durationMs: Long): Progress =
    Progress(
        animeId,
        selectedEpisodeNumber,
        positionMs,
        durationMs,
        selectedSource.toRaw(),
        selectedVoiceoverId
    )

fun updateState(
    data: PlayerData,
    target: PlayerUiState
): PlayerUiState {
    val targetEpisode = data.getEpisode(target.selectedEpisodeNumber)
        ?: return target.copy(currentUrl = null)

    val targetSource = targetEpisode.getSource(target.selectedSource)

    val targetVoiceover = targetSource.getVoiceover(target.selectedVoiceoverId)

    return target.copy(
        selectedEpisodeNumber = targetEpisode.id,
        selectedSource = targetSource.provider,
        selectedVoiceoverId = targetVoiceover.voiceoverId,
        currentUrl = targetVoiceover.hlsByQuality(target.selectedQuality) ?: targetVoiceover.url
    )
}
