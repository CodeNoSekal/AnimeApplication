package com.dmitry.test.animeapplication.presentation.screens.verification


data class VerificationUiState(
    val isLoading: Boolean = false,
    val code: String = "",
    val codeError: String? = null,
    val formError: String? = null,
){
    val submitEnabled: Boolean get() =
        !isLoading && code.isNotBlank()
}

sealed interface VerifyEvent {
    data object NavigateToHome : VerifyEvent
}
