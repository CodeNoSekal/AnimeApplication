package com.dmitry.test.animeapplication.presentation.screens.profile

import com.dmitry.test.animeapplication.domain.models.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)