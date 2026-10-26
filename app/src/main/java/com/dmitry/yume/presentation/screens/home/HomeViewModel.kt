package com.dmitry.yume.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.repository.HomeResult
import com.dmitry.yume.domain.repository.OperationResult
import com.dmitry.yume.domain.repository.ProgressResult
import com.dmitry.yume.domain.repository.StatusResult
import com.dmitry.yume.domain.usecase.GetHomeUseCase
import com.dmitry.yume.domain.usecase.GetProgressUseCase
import com.dmitry.yume.domain.usecase.ClearAllProgressUseCase
import com.dmitry.yume.domain.usecase.ClearProgressUseCase
import com.dmitry.yume.domain.usecase.ObserveLibraryUpdatesUseCase
import com.dmitry.yume.domain.usecase.PutStatusUseCase
import com.dmitry.yume.domain.usecase.PutFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProgress: GetProgressUseCase,
    private val getHome: GetHomeUseCase,
    private val putStatus: PutStatusUseCase,
    private val putFavorite: PutFavoriteUseCase,
    observeLibraryUpdates: ObserveLibraryUpdatesUseCase,
    private val clearProgress: ClearProgressUseCase? = null,
    private val clearAllProgress: ClearAllProgressUseCase? = null,
) : ViewModel() {

    private val _progressState = MutableStateFlow<ProgressViewState>(ProgressViewState.Loading)
    val progressState: StateFlow<ProgressViewState> = _progressState.asStateFlow()
    private val _progressActionState = MutableStateFlow(ProgressActionState())
    val progressActionState = _progressActionState.asStateFlow()

    fun clearProgress(animeId: Int) {
        val action = clearProgress ?: return
        if (_progressActionState.value.isBusy) return

        val previousProgress =
            (_progressState.value as? ProgressViewState.Success)?.progress ?: return
        if (previousProgress.items.none { it.animeId == animeId }) return

        viewModelScope.launch {
            _progressActionState.value = ProgressActionState(removingAnimeId = animeId)

            when (val result = action(animeId)) {
                OperationResult.Success -> {
                    _progressActionState.value = ProgressActionState()
                    _progressState.value = ProgressViewState.Success(
                        previousProgress.copy(
                            items = previousProgress.items.filterNot { it.animeId == animeId }
                        )
                    )
                }
                is OperationResult.Error -> {
                    _progressState.value = ProgressViewState.Success(previousProgress)
                    _progressActionState.value = ProgressActionState(error = result.message)
                }
            }
        }
    }

    fun clearAllProgress() {
        val action = clearAllProgress ?: return
        if (_progressActionState.value.isBusy) return

        val previousProgress =
            (_progressState.value as? ProgressViewState.Success)?.progress ?: return
        if (previousProgress.items.isEmpty()) return

        viewModelScope.launch {
            _progressActionState.value = ProgressActionState(isClearingAll = true)

            when (val result = action()) {
                OperationResult.Success -> {
                    _progressActionState.value = ProgressActionState()

                    _progressState.value = ProgressViewState.Success(
                        previousProgress.copy(items = emptyList())
                    )
                }
                is OperationResult.Error -> {
                    _progressState.value = ProgressViewState.Success(previousProgress)
                    _progressActionState.value = ProgressActionState(error = result.message)
                }
            }
        }
    }

    fun dismissProgressActionError() {
        _progressActionState.value = _progressActionState.value.copy(error = null)
    }

    private val _homeState = MutableStateFlow<HomeViewState>(HomeViewState.Loading)
    val homeState: StateFlow<HomeViewState> = combine(
        _homeState, observeLibraryUpdates()
    ) { loaded, updates ->
        if (loaded is HomeViewState.Success) {
            val updatedIndex = loaded.homeData.ranking.map { anime ->
                updates[anime.id]?.let { update ->
                    anime.copy(
                        myStatus = update.status,
                        myScore = update.score,
                        favorite = update.favorite
                    )
                } ?: anime
            }

            val updatedHomeData = loaded.homeData.copy(ranking = updatedIndex)

            loaded.copy(homeData = updatedHomeData)
        } else loaded
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeViewState.Loading)

    private var homeJob: Job? = null
    private var progressJob: Job? = null

    init {
        load()
    }

    fun load() {
        if (homeJob?.isActive != true) {
            homeJob = viewModelScope.launch {
                if (_homeState.value !is HomeViewState.Success) {
                    _homeState.value = HomeViewState.Loading
                }

                when (val result = getHome()) {
                    is HomeResult.Success -> {
                        _homeState.value = HomeViewState.Success(result.homeData)
                    }
                    is HomeResult.Error -> {
                        if (_homeState.value !is HomeViewState.Success) {
                            _homeState.value = HomeViewState.Error(result.error)
                        }
                    }
                }
            }
        }

        if (progressJob?.isActive != true) {
            progressJob = viewModelScope.launch {
                if (_progressState.value !is ProgressViewState.Success) {
                    _progressState.value = ProgressViewState.Loading
                }
                when (val result = getProgress()) {
                    is ProgressResult.Success -> {
                        _progressState.value = ProgressViewState.Success(result.progress)
                    }
                    is ProgressResult.Error -> {
                        if (_progressState.value !is ProgressViewState.Success) {
                            _progressState.value = ProgressViewState.Error(result.message)
                        }
                    }
                }
            }
        }
    }

    fun updateProgress() {
        if (progressJob?.isActive != true) {
            progressJob = viewModelScope.launch {
                if (_progressState.value !is ProgressViewState.Success) {
                    _progressState.value = ProgressViewState.Loading
                }
                when (val result = getProgress()) {
                    is ProgressResult.Success -> {
                        _progressState.value = ProgressViewState.Success(result.progress)
                    }
                    is ProgressResult.Error -> {
                        if (_progressState.value !is ProgressViewState.Success) {
                            _progressState.value = ProgressViewState.Error(result.message)
                        }
                    }
                }
            }
        }
    }
}
