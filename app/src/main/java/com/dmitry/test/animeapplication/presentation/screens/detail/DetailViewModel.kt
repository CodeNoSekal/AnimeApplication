package com.dmitry.test.animeapplication.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.test.animeapplication.domain.repository.AnimeDetailResult
import com.dmitry.test.animeapplication.domain.usecase.GetAnimeByIdUseCase
import com.dmitry.test.animeapplication.presentation.navigation.Details
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getAnimeById: GetAnimeByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<DetailViewState>(DetailViewState.Loading)
    val state: StateFlow<DetailViewState> = _state.asStateFlow()

    private val currentId: Int = checkNotNull(savedStateHandle.get<Int>(Details.ANIME_ID))

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = DetailViewState.Loading
            when (val result = getAnimeById(currentId)) {
                is AnimeDetailResult.Success ->
                    _state.value = DetailViewState.Success(result.anime)
                is AnimeDetailResult.Error ->
                    _state.value = DetailViewState.Error(result.message)
            }
        }
    }
}