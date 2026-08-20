package com.dmitry.yume.presentation.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.Status
import com.dmitry.yume.domain.repository.AnimeDetailResult
import com.dmitry.yume.domain.repository.GenresResult
import com.dmitry.yume.domain.repository.StatusResult
import com.dmitry.yume.domain.usecase.GetAnimeCatalogUseCase
import com.dmitry.yume.domain.usecase.GetGenresUseCase
import com.dmitry.yume.domain.usecase.ObserveLibraryUpdatesUseCase
import com.dmitry.yume.presentation.screens.detail.DetailViewState
import com.dmitry.yume.presentation.screens.detail.StatusViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAnimeCatalog: GetAnimeCatalogUseCase,
    private val getGenres: GetGenresUseCase,
    private val observeLibraryUpdates: ObserveLibraryUpdatesUseCase
) : ViewModel() {

    private val _optionsState = MutableStateFlow(Options())
    val optionsState: StateFlow<Options> = _optionsState.asStateFlow()

    private val _genresState = MutableStateFlow<GenresViewState>(GenresViewState.Loading)
    val genresState: StateFlow<GenresViewState> = _genresState.asStateFlow()
    private var loadJob: Job? = null

    init {
        load()
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _genresState.value = GenresViewState.Loading

            when (val result = getGenres()) {
                is GenresResult.Success ->
                    _genresState.value = GenresViewState.Success(result.genres)
                is GenresResult.Error ->
                    _genresState.value = GenresViewState.Error(result.message)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val anime: Flow<PagingData<Anime>> =
        _optionsState
            .flatMapLatest { value ->
                getAnimeCatalog(
                    status = value.status?.toRaw(),
                    sort = value.sort.toRaw(),
                    order = value.order.toRaw()
                )
            }
            .combine(
            observeLibraryUpdates(),
        ) { pagingData, updates ->
            pagingData.map { anime ->
                updates[anime.id]?.let { update ->
                    anime.copy(
                        status = update.status,
                        favorite = update.favorite
                    )
                } ?: anime
            }
        }

    fun setOptions(value: Options){
        _optionsState.update { value }
    }
}