package com.dmitry.yume.presentation.screens.detail

import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.domain.models.AppError

sealed class DetailViewState {
    data object Loading : DetailViewState()

    data class Success(val animeDetailed: AnimeDetailed) : DetailViewState()

    data class Error(val error: AppError) : DetailViewState()
}
