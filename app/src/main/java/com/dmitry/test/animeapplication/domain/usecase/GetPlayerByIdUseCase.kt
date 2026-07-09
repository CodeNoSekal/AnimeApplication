package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.repository.PlayerRepository
import com.dmitry.test.animeapplication.domain.repository.PlayerResult
import javax.inject.Inject

class GetPlayerByIdUseCase @Inject constructor(
    private val repository: PlayerRepository
){
    suspend operator fun invoke(id: Int): PlayerResult {
        return repository.getPlayerById(id)
    }
}