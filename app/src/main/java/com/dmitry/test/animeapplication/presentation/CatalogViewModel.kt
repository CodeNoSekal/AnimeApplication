package com.dmitry.test.animeapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dmitry.test.animeapplication.domain.Anime
import com.dmitry.test.animeapplication.domain.GetAnimeCatalogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAnimeCatalogUseCase: GetAnimeCatalogUseCase
) : ViewModel() {

    val anime: Flow<PagingData<Anime>> = getAnimeCatalogUseCase().cachedIn(viewModelScope)

}