package com.dmitry.test.animeapplication.presentation.screens.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.test.animeapplication.domain.models.Provider
import com.dmitry.test.animeapplication.domain.models.Quality
import com.dmitry.test.animeapplication.domain.models.hlsByQuality
import com.dmitry.test.animeapplication.domain.repository.PlayerResult
import com.dmitry.test.animeapplication.domain.usecase.GetPlayerByIdUseCase
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
                    _state.value = PlayerViewState.Success(result.player)

                    val episode = result.player.episodes.firstOrNull { it.isAvailable } ?: run {
                        _state.value = PlayerViewState.Error("No episodes available")
                        return@launch
                    }

                    val source = episode.sources.find {it.provider == Provider.Anilibria } ?: run {
                        _state.value = PlayerViewState.Error("No sources available")
                        return@launch
                    }

                    val voiceover = source.voiceovers.firstOrNull()?: run {
                        _state.value = PlayerViewState.Error("No voiceovers available")
                        return@launch
                    }

                    val quality = voiceover.quality

                    _playerState.update {
                        it.copy(
                            selectedSource = source.provider,
                            selectedEpisodeNumber = episode.id,
                            selectedVoiceover = voiceover.voiceover,
                            selectedQuality = quality,
                            currentUrl = voiceover.hlsByQuality(quality)
                        )
                    }
                }
                is PlayerResult.Error ->
                    _state.value = PlayerViewState.Error(result.message)
            }
        }
    }

    fun selectEpisode(targetEp: Int){
        val new = _playerState.value.copy(selectedEpisodeNumber = targetEp)

        if (state.value is PlayerViewState.Success) {
            _playerState.update {
                updateState((state.value as PlayerViewState.Success).player, new)
            }
        }
    }


    fun selectSource(targetPr: Provider){
        val new = _playerState.value.copy(selectedSource = targetPr)

        if (state.value is PlayerViewState.Success) {
            _playerState.update {
                updateState((state.value as PlayerViewState.Success).player, new)
            }
        }
    }

    fun selectVoiceover(targetVo: String){
        val new = _playerState.value.copy(selectedVoiceover = targetVo)

        if (state.value is PlayerViewState.Success) {
            _playerState.update {
                updateState((state.value as PlayerViewState.Success).player, new)
            }
        }
    }

    fun selectQuality(targetQ: Quality){
        val new = _playerState.value.copy(selectedQuality = targetQ)

        if (state.value is PlayerViewState.Success) {
            _playerState.update {
                updateState((state.value as PlayerViewState.Success).player, new)
            }
        }
    }

    fun savePosition(targetPos: Long){
        _playerState.update { it.copy(currentPositionMs = targetPos) }
    }

    fun nextEpisode(){
        val currentState = _state.value
        if (currentState !is PlayerViewState.Success) return

        val player = currentState.player
        val currentEpisode = _playerState.value.selectedEpisodeNumber
        val lastEpisode = player.episodesAvailable

        if (currentEpisode < lastEpisode){
            selectEpisode(currentEpisode + 1)
        }
    }

    fun prevEpisode(){
        val currentEpisode = _playerState.value.selectedEpisodeNumber

        if (currentEpisode > 1){
            selectEpisode(currentEpisode - 1)
        }
    }
}