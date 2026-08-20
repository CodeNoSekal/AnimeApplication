package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.PlaybackCatalog

sealed class PlaybackCatalogState {
    data object Loading: PlaybackCatalogState()
    data class Success(val playbackCatalog: PlaybackCatalog): PlaybackCatalogState()
    data class Error(val message: String?): PlaybackCatalogState()
}
