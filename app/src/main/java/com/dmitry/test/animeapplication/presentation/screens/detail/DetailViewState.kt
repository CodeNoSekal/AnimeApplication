package com.dmitry.test.animeapplication.presentation.screens.detail

import com.dmitry.test.animeapplication.domain.models.AnimeDetailed

sealed class DetailViewState {
    data object Loading : DetailViewState()

    data class Success(val animeDetailed: AnimeDetailed) : DetailViewState()

    data class Error(val message: String?) : DetailViewState()
}