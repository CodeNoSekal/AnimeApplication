package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.repository.MeRepository
import com.dmitry.test.animeapplication.domain.repository.StatusResult
import javax.inject.Inject

class GetStatusByIdUseCase @Inject constructor(
    private val repository: MeRepository
) {
    suspend operator fun invoke(id: Int): StatusResult {
        return repository.getStatus(id)
    }
}