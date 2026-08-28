package com.dmitry.yume.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.repository.HomeResult
import com.dmitry.yume.domain.repository.ProgressResult
import com.dmitry.yume.domain.repository.StatusResult
import com.dmitry.yume.domain.usecase.GetHomeUseCase
import com.dmitry.yume.domain.usecase.GetProgressUseCase
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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel internal constructor(
    private val getProgress: GetProgressUseCase,
    private val getHome: GetHomeUseCase,
    private val putStatus: PutStatusUseCase,
    private val putFavorite: PutFavoriteUseCase,
    observeLibraryUpdates: ObserveLibraryUpdatesUseCase,
    private val nowMillis: () -> Long
) : ViewModel() {

    @Inject constructor(
        getProgress: GetProgressUseCase,
        getHome: GetHomeUseCase,
        putStatus: PutStatusUseCase,
        putFavorite: PutFavoriteUseCase,
        observeLibraryUpdates: ObserveLibraryUpdatesUseCase
    ) : this(getProgress, getHome, putStatus, putFavorite, observeLibraryUpdates, { System.nanoTime() / 1_000_000 })

    private val _progressState = MutableStateFlow<ProgressViewState>(ProgressViewState.Loading)
    val progressState: StateFlow<ProgressViewState> = _progressState.asStateFlow()

    private val _homeState = MutableStateFlow<HomeViewState>(HomeViewState.Loading)
    val homeState: StateFlow<HomeViewState> = combine(
        _homeState, observeLibraryUpdates()
    ) { loaded, updates ->
        if (loaded is HomeViewState.Success) {
            val hero = loaded.home.hero
            val personal = updates[hero.id]
            if (personal == null) loaded else loaded.copy(
                home = loaded.home.copy(hero = hero.copy(
                    myStatus = personal.status,
                    status = personal.status,
                    favorite = personal.favorite,
                    myScore = personal.score
                ))
            )
        } else loaded
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeViewState.Loading)

    private val _heroListState = MutableStateFlow(HeroListState())
    val heroListState = _heroListState.asStateFlow()
    private val _heroFavoriteState = MutableStateFlow(HeroFavoriteState())
    val heroFavoriteState = _heroFavoriteState.asStateFlow()

    fun toggleHeroFavorite() {
        if (_heroFavoriteState.value.isSaving || _heroListState.value.isSaving) return
        val hero = (homeState.value as? HomeViewState.Success)?.home?.hero ?: return
        _heroFavoriteState.value = HeroFavoriteState(isSaving = true)
        viewModelScope.launch {
            _heroFavoriteState.value = when (val result = putFavorite(hero.id, !hero.favorite)) {
                is StatusResult.Success -> HeroFavoriteState()
                is StatusResult.Error -> HeroFavoriteState(
                    error = result.message ?: "Не удалось изменить избранное. Попробуйте ещё раз."
                )
            }
        }
    }

    fun dismissFavoriteError() {
        _heroFavoriteState.value = _heroFavoriteState.value.copy(error = null)
    }

    fun openHeroList() {
        if (_heroFavoriteState.value.isSaving || _heroListState.value.isSaving) return
        val hero = (homeState.value as? HomeViewState.Success)?.home?.hero ?: return
        _heroListState.value = HeroListState(animeId = hero.id)
    }

    fun dismissHeroList() {
        if (!_heroListState.value.isSaving) _heroListState.value = HeroListState()
    }

    fun setHeroStatus(status: String?) {
        val current = _heroListState.value
        val id = current.animeId ?: return
        if (current.isSaving || _heroFavoriteState.value.isSaving) return
        _heroListState.value = current.copy(isSaving = true, error = null)
        viewModelScope.launch {
            when (val result = putStatus(id, status)) {
                is StatusResult.Success -> _heroListState.value = HeroListState()
                is StatusResult.Error -> _heroListState.value = current.copy(
                    error = result.message ?: "Не удалось изменить список. Попробуйте ещё раз."
                )
            }
        }
    }

    private var homeJob: Job? = null
    private var progressJob: Job? = null
    private var homeLoadedAt: Long? = null

    fun load(force: Boolean = false) {
        val isHomeStale = homeLoadedAt?.let { nowMillis() - it >= HOME_CACHE_TTL_MS } ?: true
        if (homeJob?.isActive != true && (force || isHomeStale)) {
            homeJob = viewModelScope.launch {
                // Only the first load replaces content with a loading state.
                if (_homeState.value !is HomeViewState.Success) {
                    _homeState.value = HomeViewState.Loading
                }
                when (val result = getHome()) {
                    is HomeResult.Success -> {
                        _homeState.value = HomeViewState.Success(result.home)
                        homeLoadedAt = nowMillis()
                    }
                    is HomeResult.Error -> {
                        if (_homeState.value !is HomeViewState.Success) {
                            _homeState.value = HomeViewState.Error(result.message)
                        }
                    }
                }
            }
        }

        // Progress can change in the player. Recheck it on entry without hiding the cards
        // or making the home request wait for this independent endpoint.
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

    internal companion object {
        const val HOME_CACHE_TTL_MS = 5 * 60 * 1_000L
    }
}

data class HeroListState(
    val animeId: Int? = null,
    val isSaving: Boolean = false,
    val error: String? = null
)

data class HeroFavoriteState(
    val isSaving: Boolean = false,
    val error: String? = null
)
