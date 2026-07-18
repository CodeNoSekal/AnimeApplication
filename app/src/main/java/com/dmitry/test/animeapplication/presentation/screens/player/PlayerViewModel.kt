package com.dmitry.test.animeapplication.presentation.screens.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import com.dmitry.test.animeapplication.domain.models.PlayerData
import com.dmitry.test.animeapplication.domain.models.Provider
import com.dmitry.test.animeapplication.domain.models.Quality
import com.dmitry.test.animeapplication.domain.models.Voiceover
import com.dmitry.test.animeapplication.domain.models.getAvailableEpisode
import com.dmitry.test.animeapplication.domain.models.hlsByQuality
import com.dmitry.test.animeapplication.domain.repository.CurrentProgressResult
import com.dmitry.test.animeapplication.domain.repository.PlayerResult
import com.dmitry.test.animeapplication.domain.repository.ProgressResult
import com.dmitry.test.animeapplication.domain.usecase.GetPlayerByIdUseCase
import com.dmitry.test.animeapplication.domain.usecase.GetProgressByIdUseCase
import com.dmitry.test.animeapplication.domain.usecase.PutProgressUseCase
import com.dmitry.test.animeapplication.presentation.navigation.Player
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

    fun savePosition(targetPos: Long){
        _playerState.update { it.copy(currentPositionMs = targetPos) }
    }

    fun nextEpisode(){
        val currentState = _state.value
        if (currentState !is PlayerViewState.Success) return

        val player = currentState.playerData
        val currentEpisode = _playerState.value.selectedEpisodeNumber
        val lastEpisode = player.episodesAvailable

        if (currentEpisode < lastEpisode){
            selectEpisode(currentEpisode + 1)
        }
    }

    fun prevEpisode() {
        val currentEpisode = _playerState.value.selectedEpisodeNumber

        if (currentEpisode > 1) {
            selectEpisode(currentEpisode - 1)
        }
    }

    fun saveProgress(positionMs: Long, durationMs: Long) {

        if (durationMs != C.TIME_UNSET && durationMs > 0) {
            viewModelScope.launch {
                putProgress(_playerState.value.toProgress(currentId, positionMs, durationMs))
            }
        }
    }
}