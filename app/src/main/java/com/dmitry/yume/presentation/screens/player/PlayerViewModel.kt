package com.dmitry.yume.presentation.screens.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import com.dmitry.yume.di.ApplicationScope
import com.dmitry.yume.domain.models.PlayerData
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.Quality
import com.dmitry.yume.domain.repository.CurrentProgressResult
import com.dmitry.yume.domain.repository.PlayerResult
import com.dmitry.yume.domain.usecase.GetPlayerByIdUseCase
import com.dmitry.yume.domain.usecase.GetProgressByIdUseCase
import com.dmitry.yume.domain.usecase.PutProgressUseCase
import com.dmitry.yume.presentation.navigation.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getPlayerById: GetPlayerByIdUseCase,
    private val getProgressById: GetProgressByIdUseCase,
    putProgress: PutProgressUseCase,
    @ApplicationScope applicationScope: CoroutineScope,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val currentId: Int = checkNotNull(savedStateHandle.get<Int>(Player.ANIME_ID))


    private val _state = MutableStateFlow<PlayerViewState>(PlayerViewState.Loading)
    private val _playerState = MutableStateFlow(PlayerUiState())
    private val progressSaveQueue = ProgressSaveQueue(applicationScope, putProgress::invoke)
    private var loadJob: Job? = null

    val state: StateFlow<PlayerViewState> = _state.asStateFlow()
    val playerState: StateFlow<PlayerUiState> = _playerState.asStateFlow()



    init {
        load()
    }

    fun load(){
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = PlayerViewState.Loading
            when(val result = getPlayerById(currentId)){
                is PlayerResult.Success ->{
                    val preferredPlayback = when(val progressResult = getProgressById(currentId)){
                        is CurrentProgressResult.Success -> {
                            progressResult.progress.toPreferredPlayback()
                        }
                        is CurrentProgressResult.Error -> {
                            null
                        }
                    }

                    applyPlayback(
                        playerData = result.playerData,
                        preferredPlayback = preferredPlayback
                    )
                }
                is PlayerResult.Error ->
                    _state.value = PlayerViewState.Error(result.message)
            }
        }
    }

    fun selectEpisode(targetEp: Int){
        applyPlaybackChange(
            _playerState.value.toPreferredPlayback().copy(
                episodeNumber = targetEp,
                positionMs = 0L
            )
        )
    }


    fun selectSource(targetPr: Provider){
        applyPlaybackChange(
            _playerState.value.toPreferredPlayback().copy(
                sourceProvider = targetPr,
                voiceoverId = null
            )
        )
    }

    fun selectVoiceover(targetVoiceoverId: Int){
        applyPlaybackChange(
            _playerState.value.toPreferredPlayback().copy(
                voiceoverId = targetVoiceoverId
            )
        )
    }

    fun selectQuality(targetQ: Quality){
        applyPlaybackChange(
            _playerState.value.toPreferredPlayback().copy(
                quality = targetQ
            )
        )
    }

    private fun applyPlaybackChange(preferredPlayback: PreferredPlayback) {
        val playerData =
            (_state.value as? PlayerViewState.Success)?.playerData ?: return

        applyPlayback(
            playerData = playerData,
            preferredPlayback = preferredPlayback
        )
    }

    private fun applyPlayback(
        playerData: PlayerData,
        preferredPlayback: PreferredPlayback?
    ) {
        when (val playback = resolvePlayback(playerData, preferredPlayback)) {
            is PlaybackResolution.Success -> {
                _playerState.value = playback.toPlayerUiState()
                _state.value = PlayerViewState.Success(playerData)
            }
            is PlaybackResolution.Error -> {
                _state.value = PlayerViewState.Error(playback.message)
            }
        }
    }

    fun nextEpisode() = moveEpisode(1)

    fun prevEpisode() = moveEpisode(-1)

    private fun moveEpisode(offset: Int) {
        val playerData =
            (_state.value as? PlayerViewState.Success)?.playerData ?: return

        val currentEpisode = _playerState.value.selectedEpisodeNumber
        val currentIndex = playerData.episodes.indexOfFirst {
            it.id == currentEpisode
        }

        if (currentIndex == -1) return

        val targetEpisode =
            playerData.episodes.getOrNull(currentIndex + offset) ?: return

        selectEpisode(targetEpisode.id)
    }

    fun saveProgress(playbackContext: PlaybackContext, positionMs: Long, durationMs: Long) {

        if (durationMs == C.TIME_UNSET || durationMs <= 0) return

        val safePosition = positionMs.coerceIn(0, durationMs)

        _playerState.update { state ->
            val isCurrentPlayback =
                state.selectedEpisodeNumber == playbackContext.episodeNumber &&
                        state.selectedSource == playbackContext.sourceProvider &&
                        state.selectedVoiceoverId == playbackContext.voiceoverId

            if (isCurrentPlayback) {
                state.copy(currentPositionMs = safePosition)
            } else {
                state
            }
        }

        val progress = playbackContext.toProgress(
            positionMs = positionMs.coerceIn(0, durationMs),
            durationMs = durationMs
        )

        progressSaveQueue.enqueue(progress)
    }

    override fun onCleared() {
        progressSaveQueue.close()
    }
}
