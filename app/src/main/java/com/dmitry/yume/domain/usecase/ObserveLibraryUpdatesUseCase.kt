package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.MeRepository
import javax.inject.Inject

class ObserveLibraryUpdatesUseCase @Inject constructor(
    private val repository: MeRepository
) {
    operator fun invoke() = repository.libraryUpdates
}