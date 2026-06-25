package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.repository.AuthRepository
import com.dmitry.test.animeapplication.domain.repository.AuthResult
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, displayName: String): AuthResult {
        return repository.register(email.trim(), password, displayName.trim())
    }
}