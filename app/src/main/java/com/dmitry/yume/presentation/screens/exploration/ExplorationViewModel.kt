package com.dmitry.yume.presentation.screens.exploration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.repository.ExploreResult
import com.dmitry.yume.domain.usecase.GetExploreDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExplorationViewModel @Inject constructor(
    private val getExploreData: GetExploreDataUseCase,
) : ViewModel() {

    private val _state: MutableStateFlow<ExplorationViewState> = MutableStateFlow(ExplorationViewState.Loading)
    val state: StateFlow<ExplorationViewState> = _state.asStateFlow()

    private var exploreJob: Job? = null

    init {
        load()
    }

    fun load() {
        if (exploreJob?.isActive != true) {
            exploreJob = viewModelScope.launch {
                if (_state.value !is ExplorationViewState.Loading){
                    _state.value = ExplorationViewState.Loading
                }

                when (val result = getExploreData()){
                    is ExploreResult.Success -> {
                        _state.value = ExplorationViewState.Success(result.exploreData)
                    }
                    is ExploreResult.Error -> {
                        Log.e("ExplorationViewModel","Хз что за ошибка")

                        if (_state.value !is ExplorationViewState.Success) {
                            _state.value = ExplorationViewState.Error(result.error)
                        }
                    }
                }
            }
        }
    }

}
