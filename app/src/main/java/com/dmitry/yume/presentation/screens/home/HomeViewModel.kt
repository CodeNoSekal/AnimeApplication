package com.dmitry.yume.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.repository.HomeResult
import com.dmitry.yume.domain.repository.ProgressResult
import com.dmitry.yume.domain.usecase.GetHomeUseCase
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
    private val getProgress: GetProgressUseCase,
    private val getHome: GetHomeUseCase
) : ViewModel() {

    private val _progressState = MutableStateFlow<ProgressViewState>(ProgressViewState.Loading)
    val progressState: StateFlow<ProgressViewState> = _progressState.asStateFlow()

    private val _homeState = MutableStateFlow<HomeViewState>(HomeViewState.Loading)
    val homeState: StateFlow<HomeViewState> = _homeState.asStateFlow()

    private var loadJob: Job? = null

    fun load(){
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _progressState.value = ProgressViewState.Loading
            _homeState.value = HomeViewState.Loading


            when(val result = getProgress()) {
                is ProgressResult.Success -> {
                    _progressState.value = ProgressViewState.Success(result.progress)
                }

                is ProgressResult.Error -> {
                    _progressState.value = ProgressViewState.Error(result.message)
                }
            }

            when(val result = getHome()) {
                is HomeResult.Success -> {
                    _homeState.value = HomeViewState.Success(result.home)
                }

                is HomeResult.Error -> {
                    _homeState.value = HomeViewState.Error(result.message)
                }
            }
        }
    }

}
