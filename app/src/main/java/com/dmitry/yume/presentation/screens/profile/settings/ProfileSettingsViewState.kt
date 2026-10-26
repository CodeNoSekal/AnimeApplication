package com.dmitry.yume.presentation.screens.profile.settings

data class ProfileSettingsViewState(
    val isSaving: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val emailChanged: Boolean = false,
    val loggedOut: Boolean = false,
)
