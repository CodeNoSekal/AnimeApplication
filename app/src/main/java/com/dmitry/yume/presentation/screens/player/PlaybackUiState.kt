package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.PlaybackSelection
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.ResolvedPlayback
import com.dmitry.yume.domain.models.VideoQuality
import com.dmitry.yume.domain.models.VideoStream

data class PlaybackUiState(
    val selectedEpisodeNumber: Int = 1,
    val selectedSourceProvider: Provider = Provider.Kodik,
    val selectedVoiceoverId: Int? = null,
    val selectedVoiceover: String? = null,
    val selectedQuality: VideoQuality = VideoQuality.HD,
    val currentPositionMs: Long = 0L,
    val stage: PlaybackStage = PlaybackStage.Selecting
)

sealed interface PlaybackStage {

    data object Selecting : PlaybackStage

    data object WaitingForAd : PlaybackStage

    data object ShowingAd : PlaybackStage

    data object ResolvingStream : PlaybackStage

    data class Ready(
        val resolvedPlayback: ResolvedPlayback,
        val selectedStream: VideoStream
    ) : PlaybackStage

    data class Error(
        val message: String
    ) : PlaybackStage
}


fun PlaybackResolution.Success.toPlaybackUiState(): PlaybackUiState =
    PlaybackUiState(
        selectedEpisodeNumber = episodeNumber,
        selectedSourceProvider = sourceProvider,
        selectedVoiceoverId = voiceoverId,
        selectedVoiceover = voiceover,
        currentPositionMs = positionMs
    )