package com.dmitry.yume.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.repository.AnimeDetailResult
import com.dmitry.yume.domain.repository.StatusResult
import com.dmitry.yume.domain.usecase.GetAnimeByIdUseCase
import com.dmitry.yume.domain.usecase.GetStatusByIdUseCase
import com.dmitry.yume.domain.usecase.ObserveLibraryUpdatesUseCase
import com.dmitry.yume.domain.usecase.PutFavoriteUseCase
import com.dmitry.yume.domain.usecase.PutScoreUseCase
import com.dmitry.yume.domain.usecase.PutStatusUseCase
import com.dmitry.yume.presentation.navigation.Details
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getAnimeById: GetAnimeByIdUseCase,
    private val getStatusById: GetStatusByIdUseCase,
    private val putStatus: PutStatusUseCase,
    private val putFavorite: PutFavoriteUseCase,
    private val putScore: PutScoreUseCase,
    observeLibraryUpdates: ObserveLibraryUpdatesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val currentId: Int = checkNotNull(savedStateHandle.get<Int>(Details.ANIME_ID))
    private val updates = observeLibraryUpdates()
    private val _state = MutableStateFlow<DetailViewState>(DetailViewState.Loading)
    val state = combine(_state, updates) { loaded, personalStates ->
        if (loaded is DetailViewState.Success) {
            val anime = loaded.animeDetailed
            val personal = personalStates[currentId]
            loaded.copy(animeDetailed = anime.copy(
                status = if (personal != null) personal.status else anime.status,
                favorite = personal?.favorite ?: anime.favorite,
                score = if (personal != null) personal.score else anime.score,
                relations = anime.relations.map { related ->
                    personalStates[related.id]?.let {
                        related.copy(myStatus = it.status, favorite = it.favorite, myScore = it.score)
                    } ?: related
                }
            ))
        } else loaded
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailViewState.Loading)

    private val _statusState = MutableStateFlow<StatusViewState>(StatusViewState.Loading)
    val statusState = combine(_statusState, updates) { loaded, personalStates ->
        personalStates[currentId]?.let { StatusViewState.Success(it) } ?: loaded
    }.stateIn(viewModelScope, SharingStarted.Eagerly, StatusViewState.Loading)

    private val _actionState = MutableStateFlow(DetailActionState())
    val actionState = _actionState.asStateFlow()
    private val _scoreEditor = MutableStateFlow(ScoreEditorState())
    val scoreEditor = _scoreEditor.asStateFlow()
    private val statusMutationMutex = Mutex()
    private var loadJob: Job? = null

    init { load() }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (_state.value !is DetailViewState.Success) _state.value = DetailViewState.Loading
            when (val result = getAnimeById(currentId)) {
                is AnimeDetailResult.Success -> _state.value = DetailViewState.Success(result.anime)
                is AnimeDetailResult.Error ->
                    if (_state.value !is DetailViewState.Success) _state.value = DetailViewState.Error(result.message)
            }
            statusMutationMutex.withLock {
                when (val result = getStatusById(currentId)) {
                    is StatusResult.Success -> _statusState.value = StatusViewState.Success(result.status)
                    is StatusResult.Error ->
                        if (statusState.value !is StatusViewState.Success) _statusState.value = StatusViewState.Error(result.message)
                }
            }
        }
    }

    fun putFavorite() = mutate {
        val personal = statusState.value as? StatusViewState.Success
            ?: return@mutate StatusResult.Error("Не удалось загрузить личные данные")
        putFavorite(currentId, !personal.status.favorite)
    }

    fun putStatus(status: String?) = mutate { putStatus(currentId, status) }

    fun openScoreEditor() {
        if (_actionState.value.isSaving) return
        val personal = statusState.value as? StatusViewState.Success ?: return
        _actionState.value = DetailActionState()
        _scoreEditor.value = ScoreEditorState(isOpen = true, initialScore = personal.status.score)
    }

    fun dismissScoreEditor() {
        if (!_actionState.value.isSaving) {
            _scoreEditor.value = ScoreEditorState()
            _actionState.value = DetailActionState()
        }
    }

    fun saveScore(score: Int?) {
        if (!_scoreEditor.value.isOpen) return
        mutate(closeScoreOnSuccess = true) { putScore(currentId, score) }
    }

    fun dismissActionError() {
        _actionState.value = _actionState.value.copy(error = null)
    }

    private fun mutate(closeScoreOnSuccess: Boolean = false, request: suspend () -> StatusResult) {
        if (_actionState.value.isSaving) return
        _actionState.value = DetailActionState(isSaving = true)
        viewModelScope.launch {
            try {
                statusMutationMutex.withLock {
                    when (val result = request()) {
                        is StatusResult.Success -> {
                            _statusState.value = StatusViewState.Success(result.status)
                            if (closeScoreOnSuccess) _scoreEditor.value = ScoreEditorState()
                            _actionState.value = DetailActionState()
                        }
                        is StatusResult.Error -> _actionState.value = DetailActionState(
                            error = result.message ?: "Не удалось сохранить изменения"
                        )
                    }
                }
            } finally {
                _actionState.value = _actionState.value.copy(isSaving = false)
            }
        }
    }
}
