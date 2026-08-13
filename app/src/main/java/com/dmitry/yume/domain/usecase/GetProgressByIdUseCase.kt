package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.CurrentProgressResult
import com.dmitry.yume.domain.repository.MeRepository
import javax.inject.Inject


class GetProgressByIdUseCase @Inject constructor(
    private val repository: MeRepository
) {
    suspend operator fun invoke(id: Int): CurrentProgressResult {
        return repository.getProgressById(id)
    }
}