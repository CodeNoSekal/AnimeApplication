package com.dmitry.test.animeapplication.presentation.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.dmitry.test.animeapplication.domain.models.Anime
import com.dmitry.test.animeapplication.domain.usecase.GetAnimeCatalogUseCase
import com.dmitry.test.animeapplication.domain.usecase.ObserveLibraryUpdatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAnimeCatalog: GetAnimeCatalogUseCase,
    private val observeLibraryUpdates: ObserveLibraryUpdatesUseCase
) : ViewModel() {

    val anime: Flow<PagingData<Anime>> =
        combine(
            getAnimeCatalog(),
            observeLibraryUpdates(),
        ){ pagingData, updates ->
            pagingData.map { anime ->
                val update = updates[anime.id]

                if (update != null) {
                    anime.copy(
                        status = update.status,
                        favorite = update.favorite
                    )
                } else {
                    anime
                }
            }
        }.cachedIn(viewModelScope)

}