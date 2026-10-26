package com.dmitry.yume.presentation.screens.profile.edit

enum class UsernameCheck { Idle, Checking, Available, Taken }

data class EditProfileViewState(
    val displayName: String = "",
    val username: String = "",
    val usernameCheck: UsernameCheck = UsernameCheck.Idle,
    val isSaving: Boolean = false,
    val isAvatarSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)
