package com.dmitry.test.animeapplication.presentation.screens.search

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dmitry.test.animeapplication.domain.usecase.SearchAnimeUseCase
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
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val search: SearchAnimeUseCase
) : ViewModel() {
    private val _queryState = MutableStateFlow(TextFieldValue())
    val query: StateFlow<TextFieldValue> = _queryState.asStateFlow()

    fun onQueryChange(value: TextFieldValue) {
        _queryState.value = value
    }

    fun clearQuery() {
        _queryState.value = TextFieldValue()
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