package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.repository.AuthRepository
import com.dmitry.test.animeapplication.domain.repository.AuthResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        return repository.login(email.trim(), password)
    }
}