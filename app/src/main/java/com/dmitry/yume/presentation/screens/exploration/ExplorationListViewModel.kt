package com.dmitry.yume.presentation.screens.exploration

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.dmitry.yume.domain.usecase.GetExplorationListUseCase
import com.dmitry.yume.domain.usecase.ObserveLibraryUpdatesUseCase
import com.dmitry.yume.presentation.navigation.ExplorationList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class ExplorationListViewModel @Inject constructor(
    private val getExplorationList: GetExplorationListUseCase,
    observeLibraryUpdates: ObserveLibraryUpdatesUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val currentEndpoint: String = checkNotNull(
        savedStateHandle.get<String>(ExplorationList.ENDPOINT)
    )

    val title: String = savedStateHandle.get<String>(ExplorationList.TITLE).orEmpty()

    private val libraryUpdates = observeLibraryUpdates()

    val anime = getExplorationList(currentEndpoint)
        .cachedIn(viewModelScope)
        .combine(libraryUpdates) { pagingData, updates ->
            pagingData.map { anime ->
                updates[anime.id]?.let { update ->
                    anime.copy(
                        myStatus = update.status,
                        myScore = update.score,
                        favorite = update.favorite
                    )
                }?: anime
            }
        }
        .cachedIn(viewModelScope)
}