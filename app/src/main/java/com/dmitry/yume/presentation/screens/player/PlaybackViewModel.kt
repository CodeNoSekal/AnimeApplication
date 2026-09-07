package com.dmitry.yume.presentation.screens.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import com.dmitry.yume.di.ApplicationScope
import com.dmitry.yume.domain.models.PlaybackCatalog
import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.domain.models.PlaybackSelection
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.VideoQuality
import com.dmitry.yume.domain.repository.CurrentProgressResult
import com.dmitry.yume.domain.repository.PlaybackCatalogResult
import com.dmitry.yume.domain.repository.ResolvedPlaybackResult
import com.dmitry.yume.domain.usecase.GetPlaybackCatalogUseCase
import com.dmitry.yume.domain.usecase.GetAnimeByIdUseCase
import com.dmitry.yume.domain.usecase.ResolvePlaybackUseCase
import com.dmitry.yume.domain.usecase.GetProgressByIdUseCase
import com.dmitry.yume.domain.usecase.PutProgressUseCase
import com.dmitry.yume.presentation.navigation.PlaybackDestination
import com.dmitry.yume.domain.repository.AnimeDetailResult
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
    private val getAnimeById: GetAnimeByIdUseCase,
    private val getProgressById: GetProgressByIdUseCase,
    private val resolvePlayback: ResolvePlaybackUseCase,
    putProgress: PutProgressUseCase,
    @ApplicationScope applicationScope: CoroutineScope,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val currentAnimeId: Int = checkNotNull(savedStateHandle.get<Int>(PlaybackDestination.ANIME_ID))


    private val _catalogState = MutableStateFlow<PlaybackCatalogState>(PlaybackCatalogState.Loading)
    private val _playbackUiState = MutableStateFlow(PlaybackUiState())
    private val _animeDetails = MutableStateFlow<AnimeDetailed?>(null)
    private val progressSaveQueue = ProgressSaveQueue(applicationScope, putProgress::invoke)
    private var loadJob: Job? = null
    private var resolveJob: Job? = null

    val catalogState: StateFlow<PlaybackCatalogState> = _catalogState.asStateFlow()
    val playbackUiState: StateFlow<PlaybackUiState> = _playbackUiState.asStateFlow()
    val animeDetails: StateFlow<AnimeDetailed?> = _animeDetails.asStateFlow()


    init {
        load()
        loadAnimeDetails()
    }

    private fun loadAnimeDetails() {
        viewModelScope.launch {
            _animeDetails.value = when (val result = getAnimeById(currentAnimeId)) {
                is AnimeDetailResult.Success -> result.anime
                is AnimeDetailResult.Error -> null
            }
        }
    }

    fun load(){
        loadJob?.cancel()
        resolveJob?.cancel()
        loadJob = viewModelScope.launch {
            _catalogState.value = PlaybackCatalogState.Loading
            _playbackUiState.value = _playbackUiState.value.copy(stage = PlaybackStage.Selecting)
            when(val playbackCatalogResult = getPlaybackCatalog(currentAnimeId)){
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
                        playbackCatalog = playbackCatalogResult.playbackCatalog,
                        playbackPreference = playbackPreference
                    )
                }
                is PlaybackCatalogResult.Error ->
                    _catalogState.value = PlaybackCatalogState.Error(playbackCatalogResult.message)
            }
        }
    }

    fun resolveSelectedPlayback(){
        val state = _playbackUiState.value
        val voiceoverId = state.selectedVoiceoverId ?: return

        val selection = PlaybackSelection(
            animeId = currentAnimeId,
            episodeNumber = state.selectedEpisodeNumber,
            sourceProvider = state.selectedSourceProvider,
            voiceoverId = voiceoverId
        )

        resolveJob?.cancel()
        resolveJob = viewModelScope.launch {
            _playbackUiState.update {
                it.copy(stage = PlaybackStage.ResolvingStream)
            }

            when(val result = resolvePlayback(selection)){
                is ResolvedPlaybackResult.Success -> {
                    val playback = result.resolvedPlayback

                    val current = _playbackUiState.value
                    if (
                        current.selectedEpisodeNumber != selection.episodeNumber ||
                        current.selectedSourceProvider != selection.sourceProvider ||
                        current.selectedVoiceoverId != selection.voiceoverId
                    ){
                        return@launch
                    }

                    val stream = playback.streams
                        .firstOrNull { it.quality == current.selectedQuality}
                        ?: playback.streams.maxByOrNull {
                            qualityPriority(it.quality)
                        }

                    _playbackUiState.update {
                        if (stream == null) {
                            it.copy(
                                stage = PlaybackStage.Error(
                                    "Эпизод не доступен"
                                )
                            )
                        } else {
                            it.copy(
                                selectedQuality = stream.quality,
                                stage = PlaybackStage.Ready(
                                    resolvedPlayback = playback,
                                    selectedStream = stream
                                )
                            )
                        }
                    }
                }

                is ResolvedPlaybackResult.Error -> {
                    _playbackUiState.update {
                        it.copy(
                            stage = PlaybackStage.Error(
                                result.message ?: "Эпизод не доступен"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun qualityPriority(quality: VideoQuality): Int =
        when (quality) {
            VideoQuality.FHD -> 1080
            VideoQuality.HD -> 720
            VideoQuality.SD -> 480
            VideoQuality.NHD -> 360
            VideoQuality.Unknown -> 0
        }



    fun selectEpisode(targetEpisode: Int){
        if (_playbackUiState.value.selectedEpisodeNumber != targetEpisode) {
            applyPlaybackChange(
                _playbackUiState.value.toPlaybackPreference().copy(
                    episodeNumber = targetEpisode,
                    positionMs = 0L
                )
            )
        }
    }


    fun selectSource(targetProvider: Provider){
        if (_playbackUiState.value.selectedSourceProvider != targetProvider) {
            applyPlaybackChange(
                _playbackUiState.value.toPlaybackPreference().copy(
                    sourceProvider = targetProvider,
                    voiceoverId = null
                )
            )
        }
    }

    fun selectVoiceover(targetVoiceoverId: Int){
        if (_playbackUiState.value.selectedVoiceoverId != targetVoiceoverId){
            applyPlaybackChange(
                _playbackUiState.value.toPlaybackPreference().copy(
                    voiceoverId = targetVoiceoverId
                )
            )
        }
    }

    fun selectQuality(targetQuality: VideoQuality){
        _playbackUiState.update { state ->
            val ready = state.stage as? PlaybackStage.Ready
                ?: return@update state

            val stream = ready.resolvedPlayback.streams
                .firstOrNull { it.quality == targetQuality }
                ?: return@update state

            state.copy(
                selectedQuality = stream.quality,
                stage = ready.copy(selectedStream = stream)
            )
        }
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
        when (val playback = selectPlayback(playbackCatalog, playbackPreference)) {
            is PlaybackResolution.Success -> {
                _catalogState.value =
                    PlaybackCatalogState.Success(playbackCatalog)

                _playbackUiState.value =
                    playback.toPlaybackUiState().copy(
                        stage = PlaybackStage.ResolvingStream
                    )

                resolveSelectedPlayback()
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

        val currentEpisode = _playbackUiState.value.selectedEpisodeNumber
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

        _playbackUiState.update { state ->
            val isCurrentPlayback =
                state.selectedEpisodeNumber == playbackContext.episodeNumber &&
                        state.selectedSourceProvider == playbackContext.sourceProvider &&
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
