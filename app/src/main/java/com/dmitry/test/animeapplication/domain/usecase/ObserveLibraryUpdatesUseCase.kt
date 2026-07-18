package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.repository.MeRepository
import javax.inject.Inject

class ObserveLibraryUpdatesUseCase @Inject constructor(
    private val repository: MeRepository
) {
    operator fun invoke() = repository.libraryUpdates
}