package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.HomeResult
import com.dmitry.yume.domain.repository.MetaRepository
import javax.inject.Inject

class GetHomeUseCase @Inject constructor(
    private val repository: MetaRepository
) {
    suspend operator fun invoke(): HomeResult {
        return repository.getHome()
    }
}