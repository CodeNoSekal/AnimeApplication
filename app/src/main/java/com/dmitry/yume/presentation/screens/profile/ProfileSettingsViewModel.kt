package com.dmitry.yume.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.repository.AuthRepository
import com.dmitry.yume.domain.repository.OperationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileSettingsUiState(
    val isSaving: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val emailChanged: Boolean = false,
    val loggedOut: Boolean = false,
)

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileSettingsUiState())
    val state = _state.asStateFlow()

    fun changeEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(error = "Заполните почту и текущий пароль") }
            return
        }
        runAction(
            action = { repository.changeEmail(email, password) },
            successMessage = "Код подтверждения отправлен на новую почту",
            emailChanged = true,
        )
    }

    fun changePassword(current: String, new: String, confirmation: String) {
        when {
            current.isBlank() -> _state.update { it.copy(error = "Введите текущий пароль") }
            new.length < 8 -> _state.update { it.copy(error = "Новый пароль должен содержать минимум 8 символов") }
            new != confirmation -> _state.update { it.copy(error = "Пароли не совпадают") }
            else -> runAction(
                action = { repository.changePassword(current, new) },
                successMessage = "Пароль изменён",
            )
        }
    }

    fun logoutAll() {
        runAction(
            action = repository::logoutAll,
            successMessage = "Все сеансы завершены",
            loggedOut = true,
        )
    }

    fun consumeMessage() {
        _state.update { it.copy(message = null, emailChanged = false, loggedOut = false) }
    }

    fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    private fun runAction(
        action: suspend () -> OperationResult,
        successMessage: String,
        emailChanged: Boolean = false,
        loggedOut: Boolean = false,
    ) {
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null, message = null) }
            when (val result = action()) {
                OperationResult.Success -> _state.update {
                    it.copy(
                        isSaving = false,
                        message = successMessage,
                        emailChanged = emailChanged,
                        loggedOut = loggedOut,
                    )
                }
                is OperationResult.Error -> _state.update {
                    it.copy(isSaving = false, error = result.message)
                }
            }
        }
    }
}
