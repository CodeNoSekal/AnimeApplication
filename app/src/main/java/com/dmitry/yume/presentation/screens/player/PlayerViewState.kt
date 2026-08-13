package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.PlayerData

sealed class PlayerViewState {
    data object Loading: PlayerViewState()
    data class Success(val playerData: PlayerData): PlayerViewState()
    data class Error(val message: String?): PlayerViewState()
}
