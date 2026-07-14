package com.dmitry.test.animeapplication.domain.repository

import com.dmitry.test.animeapplication.domain.models.PlayerData

interface PlayerRepository {
    suspend fun getPlayerById(id: Int): PlayerResult
}

sealed interface PlayerResult {
    data class Success(
        val playerData: PlayerData
    ) : PlayerResult
    data class Error(
        val message: String?
    ) : PlayerResult
}

