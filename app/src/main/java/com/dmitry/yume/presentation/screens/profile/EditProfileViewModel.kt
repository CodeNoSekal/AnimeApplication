package com.dmitry.yume.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.domain.models.User
import com.dmitry.yume.domain.repository.AuthRepository
import com.dmitry.yume.domain.repository.OperationResult
import com.dmitry.yume.domain.repository.ProfileDataResult
import com.dmitry.yume.domain.usecase.ObserveSessionStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UsernameCheck { Idle, Checking, Available, Taken }

data class EditProfileUiState(
    val displayName: String = "",
    val username: String = "",
    val usernameCheck: UsernameCheck = UsernameCheck.Idle,
    val isSaving: Boolean = false,
    val isAvatarSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: AuthRepository,
    observeSessionState: ObserveSessionStateUseCase,
) : ViewModel() {

    val user: StateFlow<User?> = observeSessionState()
        .map { (it as? SessionState.Authenticated)?.user }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _state = MutableStateFlow(EditProfileUiState())
    val state = _state.asStateFlow()
    private var originalUsername = ""
    private var initialized = false
    private var usernameJob: Job? = null

    init {
        viewModelScope.launch {
            user.collect { current ->
                if (current != null && !initialized) {
                    initialized = true
                    originalUsername = current.username
                    _state.update {
                        it.copy(
                            displayName = current.displayName,
                            username = current.username,
                            usernameCheck = UsernameCheck.Available,
                        )
                    }
                }
            }
        }
    }

    fun onDisplayNameChange(value: String) {
        _state.update { it.copy(displayName = value, error = null, saved = false) }
    }

    fun onUsernameChange(value: String) {
        val normalized = value.lowercase().filter { it in 'a'..'z' || it.isDigit() || it == '_' }
        _state.update {
            it.copy(username = normalized, usernameCheck = UsernameCheck.Idle, error = null, saved = false)
        }
        usernameJob?.cancel()
        if (normalized == originalUsername) {
            _state.update { it.copy(usernameCheck = UsernameCheck.Available) }
        } else if (normalized.length >= 3) {
            usernameJob = viewModelScope.launch {
                delay(400)
                _state.update { it.copy(usernameCheck = UsernameCheck.Checking) }
                when (val result = repository.checkUsername(normalized)) {
                    is ProfileDataResult.Success -> _state.update {
                        it.copy(usernameCheck = if (result.data) UsernameCheck.Available else UsernameCheck.Taken)
                    }
                    is ProfileDataResult.Error -> _state.update {
                        it.copy(usernameCheck = UsernameCheck.Idle, error = result.message)
                    }
                }
            }
        }
    }

    fun suggestUsername() {
        val name = _state.value.displayName.trim()
        if (name.isBlank()) return
        viewModelScope.launch {
            when (val result = repository.suggestUsername(name)) {
                is ProfileDataResult.Success -> onUsernameChange(result.data)
                is ProfileDataResult.Error -> _state.update { it.copy(error = result.message) }
            }
        }
    }

    fun save() {
        val current = _state.value
        when {
            current.displayName.isBlank() -> {
                _state.update { it.copy(error = "Введите отображаемое имя") }
                return
            }
            current.username.length < 3 -> {
                _state.update { it.copy(error = "Логин должен содержать минимум 3 символа") }
                return
            }
            current.usernameCheck == UsernameCheck.Taken -> {
                _state.update { it.copy(error = "Этот логин уже занят") }
                return
            }
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            when (val result = repository.updateProfile(current.username, current.displayName)) {
                OperationResult.Success -> {
                    originalUsername = current.username
                    _state.update { it.copy(isSaving = false, saved = true) }
                }
                is OperationResult.Error -> _state.update {
                    it.copy(isSaving = false, error = result.message)
                }
            }
        }
    }

    fun uploadAvatar(bytes: ByteArray, mimeType: String) {
        if (mimeType !in setOf("image/jpeg", "image/png", "image/webp")) {
            _state.update { it.copy(error = "Поддерживаются только JPG, PNG и WebP") }
            return
        }
        if (bytes.size > 5 * 1024 * 1024) {
            _state.update { it.copy(error = "Файл слишком большой. Максимум 5 МБ") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isAvatarSaving = true, error = null) }
            when (val result = repository.uploadAvatar(bytes, mimeType)) {
                OperationResult.Success -> _state.update { it.copy(isAvatarSaving = false) }
                is OperationResult.Error -> _state.update {
                    it.copy(isAvatarSaving = false, error = result.message)
                }
            }
        }
    }

    fun removeAvatar() {
        viewModelScope.launch {
            _state.update { it.copy(isAvatarSaving = true, error = null) }
            when (val result = repository.removeAvatar()) {
                OperationResult.Success -> _state.update { it.copy(isAvatarSaving = false) }
                is OperationResult.Error -> _state.update {
                    it.copy(isAvatarSaving = false, error = result.message)
                }
            }
        }
    }

    fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    fun reportError(message: String) {
        _state.update { it.copy(error = message) }
    }
}
