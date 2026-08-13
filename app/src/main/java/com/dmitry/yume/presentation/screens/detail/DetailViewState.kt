package com.dmitry.yume.presentation.screens.detail

import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.domain.models.Status

sealed class DetailViewState {
    data object Loading : DetailViewState()

    data class Success(val animeDetailed: AnimeDetailed) : DetailViewState()

    data class Error(val message: String?) : DetailViewState()
}

sealed class StatusViewState {
    data object Loading : StatusViewState()

    data class Success(val status: Status) : StatusViewState()

    data class Error(val message: String?) : StatusViewState()
}