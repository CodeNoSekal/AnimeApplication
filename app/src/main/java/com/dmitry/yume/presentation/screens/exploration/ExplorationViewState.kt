package com.dmitry.yume.presentation.screens.exploration

import com.dmitry.yume.domain.models.AppError
import com.dmitry.yume.domain.models.ExploreData

sealed class ExplorationViewState {

    data object Loading : ExplorationViewState()

    data class Success(val exploreData: ExploreData) : ExplorationViewState()

    data class Error(val error: AppError) : ExplorationViewState()
}
