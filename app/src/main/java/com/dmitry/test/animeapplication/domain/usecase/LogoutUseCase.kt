package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.logout()
}