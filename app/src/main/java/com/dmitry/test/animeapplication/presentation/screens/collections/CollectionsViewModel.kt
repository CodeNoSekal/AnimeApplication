package com.dmitry.test.animeapplication.presentation.screens.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dmitry.test.animeapplication.domain.usecase.GetAnimeListByFavoriteUseCase
import com.dmitry.test.animeapplication.domain.usecase.GetAnimeListByStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val getByStatus: GetAnimeListByStatusUseCase,
    private val getByFavorite: GetAnimeListByFavoriteUseCase
) : ViewModel() {

    val animeByTab = CollectionTab.entries.associateWith { tab ->
        val flow = when(tab) {
            CollectionTab.FAVORITES -> getByFavorite(null)
            else -> getByStatus(requireNotNull(tab.status), null)
        }

        flow.cachedIn(viewModelScope)
    }
}