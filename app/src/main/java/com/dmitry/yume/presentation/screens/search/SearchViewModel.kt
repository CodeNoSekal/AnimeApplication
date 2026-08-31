package com.dmitry.yume.presentation.screens.search

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dmitry.yume.domain.usecase.SearchAnimeUseCase
import com.dmitry.yume.domain.usecase.GetAnimeByIdUseCase
import com.dmitry.yume.domain.repository.AnimeDetailResult
import com.dmitry.yume.domain.repository.SearchHistoryRepository
import com.dmitry.yume.domain.models.Anime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val search: SearchAnimeUseCase,
    private val getAnimeById: GetAnimeByIdUseCase,
    private val historyRepository: SearchHistoryRepository,
) : ViewModel() {
    private val _queryState = MutableStateFlow(TextFieldValue())
    val query: StateFlow<TextFieldValue> = _queryState.asStateFlow()

    fun onQueryChange(value: TextFieldValue) {
        _queryState.value = value
    }

    fun clearQuery() {
        _queryState.value = TextFieldValue()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val history = historyRepository.animeIds
        .mapLatest { ids ->
            val items = coroutineScope {
                ids.map { id ->
                    async {
                        when (val result = getAnimeById(id)) {
                            is AnimeDetailResult.Success -> result.anime.let { anime ->
                                Anime(
                                    id = anime.id,
                                    title = anime.title,
                                    titleEn = anime.titleEn,
                                    posterUrl = anime.posterUrl,
                                    year = anime.year,
                                    rating = anime.rating,
                                    status = anime.releaseStatus,
                                    favorite = anime.favorite,
                                    kind = anime.kind,
                                    myStatus = anime.status,
                                    myScore = anime.score,
                                )
                            }
                            is AnimeDetailResult.Error -> null
                        }
                    }
                }.awaitAll().filterNotNull()
            }
            SearchHistoryUiState(items = items, isLoading = false)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchHistoryUiState())

    fun addToHistory(animeId: Int) {
        viewModelScope.launch { historyRepository.add(animeId) }
    }

    fun removeFromHistory(animeId: Int) {
        viewModelScope.launch { historyRepository.remove(animeId) }
    }

    fun clearHistory() {
        viewModelScope.launch { historyRepository.clear() }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResult =
        _queryState
            .map { it.text }
            .debounce(300.milliseconds)
            .distinctUntilChanged()
            .flatMapLatest { q ->
                if (q.isBlank()) flowOf(PagingData.empty())
                else search(q)
            }
            .cachedIn(viewModelScope)
}
