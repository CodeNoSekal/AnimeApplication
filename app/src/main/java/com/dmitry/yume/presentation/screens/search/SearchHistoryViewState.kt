package com.dmitry.yume.presentation.screens.search

import com.dmitry.yume.domain.models.Anime

data class SearchHistoryViewState(
    val items: List<Anime> = emptyList(),
    val isLoading: Boolean = true,
)
