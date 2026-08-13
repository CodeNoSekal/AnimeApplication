package com.dmitry.yume.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.repository.ProgressResult
import com.dmitry.yume.domain.usecase.GetProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProgress: GetProgressUseCase
) : ViewModel() {

    private val _progressState = MutableStateFlow<ProgressViewState>(ProgressViewState.Loading)
    val progressState: StateFlow<ProgressViewState> = _progressState.asStateFlow()
    private var loadJob: Job? = null

    fun load(){
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _progressState.value = ProgressViewState.Loading


            when(val result = getProgress()) {
                is ProgressResult.Success -> {
                    _progressState.value = ProgressViewState.Success(result.progress)
                }

                is ProgressResult.Error -> {
                    _progressState.value = ProgressViewState.Error(result.message)
                }
            }
        }
    }

}
