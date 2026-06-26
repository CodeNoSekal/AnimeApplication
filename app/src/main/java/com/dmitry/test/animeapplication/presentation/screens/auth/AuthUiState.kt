package com.dmitry.test.animeapplication.presentation.screens.auth

enum class AuthMode { Login, Register }

data class AuthUiState(
    val mode: AuthMode = AuthMode.Login,
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val displayNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val formError: String? = null,
) {
    val submitEnabled: Boolean get() = !isLoading && when (mode) {
        AuthMode.Login -> email.isNotBlank() && password.isNotBlank()
        AuthMode.Register -> email.isNotBlank() && password.isNotBlank() && displayName.isNotBlank()
    }
}

sealed interface AuthEvent {
    data object NavigateToVerification : AuthEvent
    data object NavigateToHome : AuthEvent
}
