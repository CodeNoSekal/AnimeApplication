package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.MeRepository
import javax.inject.Inject

class GetProfileStatisticsUseCase @Inject constructor(
    private val repository: MeRepository,
) {
    suspend operator fun invoke() = repository.getProfileStatistics()
}
