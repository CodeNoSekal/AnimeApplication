package com.dmitry.yume.presentation.screens.home

import com.dmitry.yume.domain.models.ProgressData

sealed class ProgressViewState {
    data object Loading : ProgressViewState()

    data class Success(val progress: ProgressData) : ProgressViewState()

    data class Error(val message: String?) : ProgressViewState()
}