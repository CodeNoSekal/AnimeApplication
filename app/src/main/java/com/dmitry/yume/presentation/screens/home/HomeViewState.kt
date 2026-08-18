package com.dmitry.yume.presentation.screens.home

import com.dmitry.yume.domain.models.Home

sealed class HomeViewState {
    data object Loading : HomeViewState()

    data class Success(val home: Home) : HomeViewState()

    data class Error(val message: String?) : HomeViewState()
}