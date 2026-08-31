package com.dmitry.yume.presentation.screens.profile

import com.dmitry.yume.domain.models.ProfileStatistics

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isStatsLoading: Boolean = false,
    val statistics: ProfileStatistics? = null,
    val error: String? = null
)
