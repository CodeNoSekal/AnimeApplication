package com.dmitry.yume.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.repository.AnimeDetailResult
import com.dmitry.yume.domain.repository.StatusResult
import com.dmitry.yume.domain.usecase.GetAnimeByIdUseCase
import com.dmitry.yume.domain.usecase.GetStatusByIdUseCase
import com.dmitry.yume.domain.usecase.PutFavoriteUseCase
import com.dmitry.yume.domain.usecase.PutStatusUseCase
import com.dmitry.yume.presentation.navigation.Details
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getAnimeById: GetAnimeByIdUseCase,
    private val getStatusById: GetStatusByIdUseCase,
    private val putStatus: PutStatusUseCase,
    private val putFavorite: PutFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<DetailViewState>(DetailViewState.Loading)
    val state: StateFlow<DetailViewState> = _state.asStateFlow()

    private val _statusState = MutableStateFlow<StatusViewState>(StatusViewState.Loading)
    val statusState: StateFlow<StatusViewState> = _statusState.asStateFlow()

    private val currentId: Int = checkNotNull(savedStateHandle.get<Int>(Details.ANIME_ID))
    private val statusMutationMutex = Mutex()
    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = DetailViewState.Loading
            _statusState.value = StatusViewState.Loading

            when (val result = getAnimeById(currentId)) {
                is AnimeDetailResult.Success ->
                    _state.value = DetailViewState.Success(result.anime)
                is AnimeDetailResult.Error ->
                    _state.value = DetailViewState.Error(result.message)
            }

            statusMutationMutex.withLock {
                when (val statusResult = getStatusById(currentId)) {
                    is StatusResult.Success ->
                        _statusState.value = StatusViewState.Success(statusResult.status)
                    is StatusResult.Error ->
                        _statusState.value = StatusViewState.Error(statusResult.message)
                }
            }
        }
    }

    fun putFavorite() {
        viewModelScope.launch {
            statusMutationMutex.withLock {
                val currentStatus = statusState.value as? StatusViewState.Success
                    ?: return@withLock
                val favorite = currentStatus.status.favorite

                when (val statusResult = putFavorite(currentId, !favorite)) {
                    is StatusResult.Success ->
                        _statusState.value = StatusViewState.Success(statusResult.status)
                    is StatusResult.Error ->
                        _statusState.value = StatusViewState.Error(statusResult.message)
                }
            }
        }
    }

    fun putStatus(status: String?) {
        viewModelScope.launch {
            statusMutationMutex.withLock {
                when (val statusResult = putStatus(currentId, status)) {
                    is StatusResult.Success ->
                        _statusState.value = StatusViewState.Success(statusResult.status)
                    is StatusResult.Error ->
                        _statusState.value = StatusViewState.Error(statusResult.message)
                }
            }
        }
    }
}
