package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.PlayerRepository
import com.dmitry.yume.domain.repository.PlayerResult
import javax.inject.Inject

class GetPlayerByIdUseCase @Inject constructor(
    private val repository: PlayerRepository
){
    suspend operator fun invoke(id: Int): PlayerResult {
        return repository.getPlayerById(id)
    }
}