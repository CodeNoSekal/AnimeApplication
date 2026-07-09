package com.dmitry.test.animeapplication.domain.repository

import com.dmitry.test.animeapplication.domain.models.Player

interface PlayerRepository {
    suspend fun getPlayerById(id: Int): PlayerResult
}

sealed interface PlayerResult {
    data class Success(
        val player: Player
    ) : PlayerResult
    data class Error(
        val message: String?
    ) : PlayerResult
}

