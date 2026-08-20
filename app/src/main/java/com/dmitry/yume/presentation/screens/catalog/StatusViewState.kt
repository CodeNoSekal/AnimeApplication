package com.dmitry.yume.presentation.screens.catalog

import com.dmitry.yume.domain.models.Genres

sealed class GenresViewState {
    data object Loading : GenresViewState()

    data class Success(val genres: Genres) : GenresViewState()

    data class Error(val message: String?) : GenresViewState()
}