package com.dmitry.yume.presentation.screens.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import com.dmitry.yume.domain.models.PlayerData
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.Quality
import com.dmitry.yume.domain.models.Voiceover
import com.dmitry.yume.domain.models.getAvailableEpisode
import com.dmitry.yume.domain.models.hlsByQuality
import com.dmitry.yume.domain.repository.CurrentProgressResult
import com.dmitry.yume.domain.repository.PlayerResult
import com.dmitry.yume.domain.repository.ProgressResult
import com.dmitry.yume.domain.usecase.GetPlayerByIdUseCase
import com.dmitry.yume.domain.usecase.GetProgressByIdUseCase
import com.dmitry.yume.domain.usecase.PutProgressUseCase
import com.dmitry.yume.presentation.navigation.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getPlayerById: GetPlayerByIdUseCase,
    private val getProgressById: GetProgressByIdUseCase,
    private val putProgress: PutProgressUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val currentId: Int = checkNotNull(savedStateHandle.get<Int>(Player.ANIME_ID))


    private val _state = MutableStateFlow<PlayerViewState>(PlayerViewState.Loading)
    private val _playerState = MutableStateFlow(PlayerUiState())

    val state: StateFlow<PlayerViewState> = _state.asStateFlow()
    val playerState: StateFlow<PlayerUiState> = _playerState.asStateFlow()



    init {
        load()
    }

    fun load(){
        viewModelScope.launch {
            _state.value = PlayerViewState.Loading
            when(val result = getPlayerById(currentId)){
                is PlayerResult.Success ->{
                    _state.value = PlayerViewState.Success(result.playerData)

                    if (result.playerData.getAvailableEpisode() == null){
                        _state.value = PlayerViewState.Error("No episodes available")
                        return@launch
                    }

                    when(val progressResult = getProgressById(currentId)){
                        is CurrentProgressResult.Success -> {

                            val preferred = progressResult.progress.toPreferredPlayback()

                            val initialState = resolveInitialState(result.playerData, preferred)

                            _playerState.update {
                                it.copy(
                                    selectedEpisodeNumber = initialState.episodeNumber,
                                    selectedSource = initialState.sourceProvider,
                                    selectedVoiceoverId = initialState.voiceoverId,
                                    selectedQuality = initialState.quality,
                                    currentUrl = initialState.url,
                                    currentPositionMs = initialState.positionMs
                                )
                            }
                        }

                        is CurrentProgressResult.Error -> {
                            val initialState = resolveInitialState(result.playerData)

                            _playerState.update {
                                it.copy(
                                    selectedEpisodeNumber = initialState.episodeNumber,
                                    selectedSource = initialState.sourceProvider,
                                    selectedVoiceoverId = initialState.voiceoverId,
                                    selectedQuality = initialState.quality,
                                    currentUrl = initialState.url,
                                    currentPositionMs = initialState.positionMs
                                )
                            }
                        }
                    }
                }
                is PlayerResult.Error ->
                    _state.value = PlayerViewState.Error(result.message)
            }
        }
    }

    fun selectEpisode(targetEp: Int){
        val new = _playerState.value.copy(
            selectedEpisodeNumber = targetEp,
            currentPositionMs = 0
        )

        if (state.value is PlayerViewState.Success) {
            _playerState.update {
                updateState((state.value as PlayerViewState.Success).playerData, new)
            }
        }
    }


    fun selectSource(targetPr: Provider){
        val new = _playerState.value.copy(selectedSource = targetPr)

        if (state.value is PlayerViewState.Success) {
            _playerState.update {
                updateState((state.value as PlayerViewState.Success).playerData, new)
            }
        }
    }

    fun selectVoiceover(targetVoiceoverId: Int){
        val new = _playerState.value.copy(selectedVoiceoverId = targetVoiceoverId)

        if (state.value is PlayerViewState.Success) {
            _playerState.update {
                updateState((state.value as PlayerViewState.Success).playerData, new)
            }
        }
    }

    fun selectQuality(targetQ: Quality){
        val new = _playerState.value.copy(selectedQuality = targetQ)

        if (state.value is PlayerViewState.Success) {
            _playerState.update {
                updateState((state.value as PlayerViewState.Success).playerData, new)
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

        viewModelScope.launch {
            putProgress(progress)
        }
    }
}