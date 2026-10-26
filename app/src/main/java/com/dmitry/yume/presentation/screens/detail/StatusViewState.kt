package com.dmitry.yume.presentation.screens.detail

import com.dmitry.yume.domain.models.Status

sealed class StatusViewState {
    data object Loading : StatusViewState()

    data class Success(val status: Status) : StatusViewState()

    data class Error(val message: String?) : StatusViewState()
}
