package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.repository.ProfileRepository
import com.dmitry.test.animeapplication.domain.repository.ProfileResult
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke() : ProfileResult{
        return repository.getUser()
    }
}