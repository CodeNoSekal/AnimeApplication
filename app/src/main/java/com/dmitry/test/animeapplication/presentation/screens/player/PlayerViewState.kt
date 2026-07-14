package com.dmitry.test.animeapplication.presentation.screens.player

import com.dmitry.test.animeapplication.domain.models.PlayerData

sealed class PlayerViewState {
    data object Loading: PlayerViewState()
//    data object Blocked: PlayerViewState()
    data class Success(val playerData: PlayerData): PlayerViewState()
    data class Error(val message: String?): PlayerViewState()
}