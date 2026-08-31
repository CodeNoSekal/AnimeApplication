package com.dmitry.yume.presentation.screens.exploration

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.usecase.GetAnimeCatalogUseCase
import com.dmitry.yume.domain.usecase.ObserveLibraryUpdatesUseCase
import com.dmitry.yume.presentation.navigation.QuickSearch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class QuickSearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getAnimeCatalog: GetAnimeCatalogUseCase,
    observeLibraryUpdates: ObserveLibraryUpdatesUseCase,
) : ViewModel() {

    val category = QuickSearchCategory.fromRoute(
        savedStateHandle.get<String>(QuickSearch.CATEGORY)
    )

    val anime: Flow<PagingData<Anime>> = getAnimeCatalog(category.options)
        .cachedIn(viewModelScope)
        .combine(observeLibraryUpdates()) { pagingData, updates ->
            pagingData.map { anime ->
                updates[anime.id]?.let { update ->
                    anime.copy(
                        myStatus = update.status,
                        myScore = update.score,
                        favorite = update.favorite,
                    )
                } ?: anime
            }
        }
        .cachedIn(viewModelScope)
}
