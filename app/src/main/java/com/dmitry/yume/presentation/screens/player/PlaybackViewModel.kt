package com.dmitry.yume.presentation.screens.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import com.dmitry.yume.di.ApplicationScope
import com.dmitry.yume.domain.models.PlaybackCatalog
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.VideoQuality
import com.dmitry.yume.domain.repository.CurrentProgressResult
import com.dmitry.yume.domain.repository.PlaybackCatalogResult
import com.dmitry.yume.domain.usecase.GetPlaybackCatalogUseCase
import com.dmitry.yume.domain.usecase.ResolvePlaybackUseCase
import com.dmitry.yume.domain.usecase.GetProgressByIdUseCase
import com.dmitry.yume.domain.usecase.PutProgressUseCase
import com.dmitry.yume.presentation.navigation.PlaybackDestination
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
class PlaybackViewModel @Inject constructor(
    private val getPlaybackCatalog: GetPlaybackCatalogUseCase,
    private val getProgressById: GetProgressByIdUseCase,
    private val resolvePlayback: ResolvePlaybackUseCase,
    putProgress: PutProgressUseCase,
    @ApplicationScope applicationScope: CoroutineScope,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val currentAnimeId: Int = checkNotNull(savedStateHandle.get<Int>(PlaybackDestination.ANIME_ID))


    private val _catalogState = MutableStateFlow<PlaybackCatalogState>(PlaybackCatalogState.Loading)
    private val _playbackState = MutableStateFlow(PlaybackUiState())
    private val progressSaveQueue = ProgressSaveQueue(applicationScope, putProgress::invoke)
    private var loadJob: Job? = null

    val catalogState: StateFlow<PlaybackCatalogState> = _catalogState.asStateFlow()
    val playbackState: StateFlow<PlaybackUiState> = _playbackState.asStateFlow()



    init {
        load()
    }

    fun load(){
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _catalogState.value = PlaybackCatalogState.Loading
            when(val result = getPlaybackCatalog(currentAnimeId)){
                is PlaybackCatalogResult.Success -> {
                    val playbackPreference = when(val progressResult = getProgressById(currentAnimeId)){
                        is CurrentProgressResult.Success -> {
                            progressResult.progress.toPlaybackPreference()
                        }
                        is CurrentProgressResult.Error -> {
                            null
                        }
                    }

                    applyPlayback(
                        playbackCatalog = result.playbackCatalog,
                        playbackPreference = playbackPreference
                    )
                }
                is PlaybackCatalogResult.Error ->
                    _catalogState.value = PlaybackCatalogState.Error(result.message)
            }
        }
    }

    fun resolveSelectedPlayback() {

    }

    fun selectEpisode(targetEp: Int){
        applyPlaybackChange(
            _playbackState.value.toPlaybackPreference().copy(
                episodeNumber = targetEp,
                positionMs = 0L
            )
        )
    }


    fun selectSource(targetPr: Provider){
        applyPlaybackChange(
            _playbackState.value.toPlaybackPreference().copy(
                sourceProvider = targetPr,
                voiceoverId = null
            )
        )
    }

    fun selectVoiceover(targetVoiceoverId: Int){
        applyPlaybackChange(
            _playbackState.value.toPlaybackPreference().copy(
                voiceoverId = targetVoiceoverId
            )
        )
    }

    fun selectQuality(targetQ: VideoQuality){
        applyPlaybackChange(
            _playbackState.value.toPlaybackPreference().copy(
                quality = targetQ
            )
        )
    }

    private fun applyPlaybackChange(playbackPreference: PlaybackPreference) {
        val playbackCatalog =
            (_catalogState.value as? PlaybackCatalogState.Success)?.playbackCatalog ?: return

        applyPlayback(
            playbackCatalog = playbackCatalog,
            playbackPreference = playbackPreference
        )
    }

    private fun applyPlayback(
        playbackCatalog: PlaybackCatalog,
        playbackPreference: PlaybackPreference?
    ) {
        when (val playback = resolvePlayback(playbackCatalog, playbackPreference)) {
            is PlaybackResolution.Success -> {
                _playbackState.value = playback.toPlaybackUiState()
                _catalogState.value = PlaybackCatalogState.Success(playbackCatalog)
            }
            is PlaybackResolution.Error -> {
                _catalogState.value = PlaybackCatalogState.Error(playback.message)
            }
        }
    }

    fun nextEpisode() = moveEpisode(1)

    fun prevEpisode() = moveEpisode(-1)

    private fun moveEpisode(offset: Int) {
        val playbackCatalog =
            (_catalogState.value as? PlaybackCatalogState.Success)?.playbackCatalog ?: return

        val currentEpisode = _playbackState.value.selectedEpisodeNumber
        val currentIndex = playbackCatalog.episodes.indexOfFirst {
            it.number == currentEpisode
        }

        if (currentIndex == -1) return

        val targetEpisode =
            playbackCatalog.episodes.getOrNull(currentIndex + offset) ?: return

        selectEpisode(targetEpisode.number)
    }

    fun saveProgress(playbackContext: PlaybackContext, positionMs: Long, durationMs: Long) {

        if (durationMs == C.TIME_UNSET || durationMs <= 0) return

        val safePosition = positionMs.coerceIn(0, durationMs)

        _playbackState.update { state ->
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
