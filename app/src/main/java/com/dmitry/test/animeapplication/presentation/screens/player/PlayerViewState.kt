package com.dmitry.test.animeapplication.presentation.screens.player

import com.dmitry.test.animeapplication.domain.models.Player

sealed class PlayerViewState {
    data object Loading: PlayerViewState()
//    data object Blocked: PlayerViewState()
    data class Success(val player: Player): PlayerViewState()
    data class Error(val message: String?): PlayerViewState()
}