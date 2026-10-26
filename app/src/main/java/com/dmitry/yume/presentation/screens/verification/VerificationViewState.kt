package com.dmitry.yume.presentation.screens.verification


data class VerificationViewState(
    val isLoading: Boolean = false,
    val code: String = "",
    val codeError: String? = null,
    val formError: String? = null,
){
    val submitEnabled: Boolean get() =
        !isLoading && code.isNotBlank()
}

sealed interface VerificationEvent {
    data object NavigateToHome : VerificationEvent
}
