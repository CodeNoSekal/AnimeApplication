package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.MeRepository
import com.dmitry.yume.domain.repository.StatusResult
import javax.inject.Inject

class PutScoreUseCase @Inject constructor(private val repository: MeRepository) {
    suspend operator fun invoke(id: Int, score: Int?): StatusResult {
        if (score != null && score !in 1..10) {
            return StatusResult.Error("Выберите оценку от 1 до 10")
        }
        return repository.putScore(id, score)
    }
}
