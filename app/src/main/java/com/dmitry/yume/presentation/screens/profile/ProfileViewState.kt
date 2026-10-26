package com.dmitry.yume.presentation.screens.profile

import com.dmitry.yume.domain.models.AppError
import com.dmitry.yume.domain.models.Profile

sealed interface ProfileViewState {
    data object Loading : ProfileViewState

    data object Guest : ProfileViewState

    data class Content(val profile: Profile) : ProfileViewState

    data class Error(val error: AppError) : ProfileViewState
}
