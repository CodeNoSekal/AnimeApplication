package com.dmitry.yume.presentation.screens.home

import com.dmitry.yume.domain.models.AppError
import com.dmitry.yume.domain.models.HomeData

sealed class HomeViewState {
    data object Loading : HomeViewState()

    data class Success(val homeData: HomeData) : HomeViewState()

    data class Error(val error: AppError) : HomeViewState()
}