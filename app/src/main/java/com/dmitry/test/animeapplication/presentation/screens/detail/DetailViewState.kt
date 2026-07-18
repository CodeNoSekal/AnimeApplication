package com.dmitry.test.animeapplication.presentation.screens.detail

import com.dmitry.test.animeapplication.domain.models.AnimeDetailed
import com.dmitry.test.animeapplication.domain.models.Status

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