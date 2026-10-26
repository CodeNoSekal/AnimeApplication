package com.dmitry.yume.presentation.screens.home

data class ProgressActionState(
    val removingAnimeId: Int? = null,
    val isClearingAll: Boolean = false,
    val error: String? = null,
) {
    val isBusy: Boolean get() = removingAnimeId != null || isClearingAll
}
