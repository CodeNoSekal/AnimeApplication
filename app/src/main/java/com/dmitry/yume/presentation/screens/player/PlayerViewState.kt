package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.data.response.ProgressItem
import com.dmitry.yume.domain.models.PlayerData
import com.dmitry.yume.domain.models.ProgressData
import com.dmitry.yume.domain.models.ProgressItemData
import com.dmitry.yume.domain.models.Provider

sealed class PlayerViewState {
    data object Loading: PlayerViewState()
//    data object Blocked: PlayerViewState()
    data class Success(val playerData: PlayerData): PlayerViewState()
    data class Error(val message: String?): PlayerViewState()
}