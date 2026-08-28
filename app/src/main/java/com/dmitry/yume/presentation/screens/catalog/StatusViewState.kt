package com.dmitry.yume.presentation.screens.catalog

import com.dmitry.yume.domain.models.Meta

sealed class GenresViewState {
    data object Loading : GenresViewState()

    data class Success(val meta: Meta) : GenresViewState()

    data class Error(val message: String?) : GenresViewState()
}