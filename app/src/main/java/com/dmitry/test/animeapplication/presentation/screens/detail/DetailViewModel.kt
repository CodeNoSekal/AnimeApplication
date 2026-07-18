package com.dmitry.test.animeapplication.presentation.screens.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.test.animeapplication.domain.repository.AnimeDetailResult
import com.dmitry.test.animeapplication.domain.repository.StatusResult
import com.dmitry.test.animeapplication.domain.usecase.GetAnimeByIdUseCase
import com.dmitry.test.animeapplication.domain.usecase.GetStatusByIdUseCase
import com.dmitry.test.animeapplication.domain.usecase.PutFavoriteUseCase
import com.dmitry.test.animeapplication.domain.usecase.PutStatusUseCase
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
    private val getStatusById: GetStatusByIdUseCase,
    private val putStatus: PutStatusUseCase,
    private val putFavorite: PutFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<DetailViewState>(DetailViewState.Loading)
    val state: StateFlow<DetailViewState> = _state.asStateFlow()

    private val _statusState = MutableStateFlow<StatusViewState>(StatusViewState.Loading)
    val statusState: StateFlow<StatusViewState> = _statusState.asStateFlow()

    private val currentId: Int = checkNotNull(savedStateHandle.get<Int>(Details.ANIME_ID))

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = DetailViewState.Loading
            _statusState.value = StatusViewState.Loading

            when (val result = getAnimeById(currentId)) {
                is AnimeDetailResult.Success ->
                    _state.value = DetailViewState.Success(result.anime)
                is AnimeDetailResult.Error ->
                    _state.value = DetailViewState.Error(result.message)
            }

            when (val statusResult = getStatusById(currentId)) {
                is StatusResult.Success ->
                    _statusState.value = StatusViewState.Success(statusResult.status)
                is StatusResult.Error ->
                    _statusState.value = StatusViewState.Error(statusResult.message)
            }
        }
    }

    fun putFavorite() {
        viewModelScope.launch {
            val currentStatus = statusState.value as? StatusViewState.Success ?: return@launch
            val favorite = currentStatus.status.favorite

            when (val statusResult = putFavorite(currentId, !favorite)) {
                is StatusResult.Success ->
                    _statusState.value = StatusViewState.Success(statusResult.status)
                is StatusResult.Error ->
                    Log.e(TAG, "putFavorite failed, id=$currentId message=${statusResult.message}")
            }
        }
    }

    fun putStatus(status: String?) {
        viewModelScope.launch {
            when (val statusResult = putStatus(currentId, status)) {
                is StatusResult.Success ->
                    _statusState.value = StatusViewState.Success(statusResult.status)
                is StatusResult.Error ->
                    Log.e(TAG, "putStatus failed, id=$currentId status=$status message=${statusResult.message}")
            }
        }
    }

    private companion object {
        const val TAG = "DetailViewModel"
    }
}
