package com.dmitry.yume.presentation.screens.catalog

import android.media.Rating
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.usecase.GetAnimeCatalogUseCase
import com.dmitry.yume.domain.usecase.ObserveLibraryUpdatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAnimeCatalog: GetAnimeCatalogUseCase,
    private val observeLibraryUpdates: ObserveLibraryUpdatesUseCase
) : ViewModel() {

    private val _optionsState = MutableStateFlow(Options())
    val optionsState: StateFlow<Options> = _optionsState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val anime: Flow<PagingData<Anime>> =
        _optionsState
            .flatMapLatest { value ->
                getAnimeCatalog(
                    status = value.status?.toRaw() ?: "",
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