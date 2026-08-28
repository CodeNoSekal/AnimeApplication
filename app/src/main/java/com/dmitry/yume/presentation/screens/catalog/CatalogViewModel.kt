package com.dmitry.yume.presentation.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.repository.GenresResult
import com.dmitry.yume.domain.usecase.GetAnimeCatalogUseCase
import com.dmitry.yume.domain.usecase.GetMetaUseCase
import com.dmitry.yume.domain.usecase.ObserveLibraryUpdatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAnimeCatalog: GetAnimeCatalogUseCase,
    private val getMeta: GetMetaUseCase,
    observeLibraryUpdates: ObserveLibraryUpdatesUseCase
) : ViewModel() {

    private val _metaState = MutableStateFlow<GenresViewState>(GenresViewState.Loading)
    private val _draftFilters = MutableStateFlow(FilterOptions())
    private val libraryUpdates = observeLibraryUpdates()
    private var pagingJob: Job? = null
    private val _catalog = MutableStateFlow(createCatalog(CatalogOptions(), generation = 0))
    val catalog: StateFlow<CatalogSession> = _catalog.asStateFlow()

    val draftFilters: StateFlow<FilterOptions> = _draftFilters.asStateFlow()
    val metaState: StateFlow<GenresViewState> = _metaState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _metaState.value = GenresViewState.Loading

            when (val result = getMeta()) {
                is GenresResult.Success ->
                    _metaState.value = GenresViewState.Success(result.meta)
                is GenresResult.Error ->
                    _metaState.value = GenresViewState.Error(result.message)
            }
        }
    }

    private fun createCatalog(options: CatalogOptions, generation: Int): CatalogSession {
        // Keep pages across navigation, but release the old cache when the query changes.
        pagingJob?.cancel()
        val job = SupervisorJob(viewModelScope.coroutineContext[Job])
        pagingJob = job
        val scope = CoroutineScope(viewModelScope.coroutineContext + job)
        val anime = getAnimeCatalog(options.toDomain())
            // combine can submit the same generation more than once for library updates.
            .cachedIn(scope)
            .combine(libraryUpdates) { pagingData, updates ->
                pagingData.map { anime ->
                    updates[anime.id]?.let { update ->
                        anime.copy(
                            myStatus = update.status,
                            myScore = update.score,
                            favorite = update.favorite
                        )
                    } ?: anime
                }
            }
            // Expose the cached snapshot directly to collectAsLazyPagingItems on re-entry.
            .cachedIn(scope)
        return CatalogSession(generation, options, anime)
    }

    private fun setCatalogOptions(options: CatalogOptions) {
        val current = _catalog.value
        if (options != current.options) {
            _catalog.value = createCatalog(options, current.generation + 1)
        }
    }

    fun setSorting(value: SortingOptions){
        setCatalogOptions(_catalog.value.options.copy(sorting = value))
    }

    fun assignGenre(id: Int) {
        _draftFilters.update { options ->
            when(id) {
                in options.genres -> options.copy(
                    genres = options.genres - id,
                    excludeGenres = options.excludeGenres + id
                )
                in options.excludeGenres -> options.copy(
                    genres = options.genres - id,
                    excludeGenres = options.excludeGenres - id
                )
                else -> options.copy(
                    genres = options.genres + id,
                    excludeGenres = options.excludeGenres - id
                )
            }
        }
    }
    fun assignType(kind: AnimeKind) {
        _draftFilters.update { options ->
            when(kind) {
                in options.kinds -> options.copy(
                    kinds = options.kinds - kind
                )
                else -> options.copy(
                    kinds = options.kinds + kind
                )
            }
        }
    }

    fun assignStatus(status: Status) {
        _draftFilters.update { options ->
            when(status) {
                in options.statuses -> options.copy(
                    statuses = options.statuses - status
                )
                else -> options.copy(
                    statuses = options.statuses + status
                )
            }
        }
    }

    fun assignCollection(collection: Collection) {
        _draftFilters.update { options ->
            when(collection) {
                in options.collections -> options.copy(
                    collections = options.collections - collection,
                    excludeCollections = options.excludeCollections + collection
                )
                in options.excludeCollections -> options.copy(
                    collections = options.collections - collection,
                    excludeCollections = options.excludeCollections - collection
                )
                else -> options.copy(
                    collections = options.collections + collection,
                    excludeCollections = options.excludeCollections - collection
                )
            }
        }
    }

    fun applyFilters() {
        setCatalogOptions(_catalog.value.options.copy(filters = _draftFilters.value))
    }

    fun dropFilters() {
        _draftFilters.value = FilterOptions()
        setCatalogOptions(_catalog.value.options.copy(filters = FilterOptions()))
    }

    fun dropGenres() {
        _draftFilters.update {
            it.copy(
                genres = emptySet(),
                excludeGenres = emptySet()
            )
        }
    }
}

data class CatalogSession(
    val generation: Int,
    val options: CatalogOptions,
    val anime: Flow<PagingData<Anime>>
)
