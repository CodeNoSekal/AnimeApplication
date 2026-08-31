package com.dmitry.yume.presentation.screens.search

import com.dmitry.yume.domain.models.Anime

data class SearchHistoryUiState(
    val items: List<Anime> = emptyList(),
    val isLoading: Boolean = true,
)
