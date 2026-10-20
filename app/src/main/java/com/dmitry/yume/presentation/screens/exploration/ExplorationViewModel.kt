package com.dmitry.yume.presentation.screens.exploration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.models.AppError
import com.dmitry.yume.domain.models.ExploreModel
import com.dmitry.yume.domain.repository.ExploreResult
import com.dmitry.yume.domain.usecase.GetExploreDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExplorationUiState {

    data object Loading : ExplorationUiState()

    data class Success(val exploreData: ExploreModel) : ExplorationUiState()

    data class Error(val error: AppError) : ExplorationUiState()
}

@HiltViewModel
class ExplorationViewModel @Inject constructor(
    private val getExploreData: GetExploreDataUseCase,
) : ViewModel() {

    private val _state: MutableStateFlow<ExplorationUiState> = MutableStateFlow(ExplorationUiState.Loading)
    val state: StateFlow<ExplorationUiState> = _state.asStateFlow()

    private var exploreJob: Job? = null

    init {
        load()
    }

    fun load() {
        if (exploreJob?.isActive != true) {
            exploreJob = viewModelScope.launch {
                if (_state.value !is ExplorationUiState.Loading){
                    _state.value = ExplorationUiState.Loading
                }

                when (val result = getExploreData()){
                    is ExploreResult.Success -> {
                        _state.value = ExplorationUiState.Success(result.exploreData)
                    }
                    is ExploreResult.Error -> {
                        Log.e("ExplorationViewModel","Хз что за ошибка")

                        if (_state.value !is ExplorationUiState.Success) {
                            _state.value = ExplorationUiState.Error(result.error)
                        }
                    }
                }
            }
        }
    }

}
