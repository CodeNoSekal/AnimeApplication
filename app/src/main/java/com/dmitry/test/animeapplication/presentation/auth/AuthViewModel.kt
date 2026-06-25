package com.dmitry.test.animeapplication.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.test.animeapplication.domain.repository.AuthResult
import com.dmitry.test.animeapplication.domain.usecase.LoginUseCase
import com.dmitry.test.animeapplication.domain.usecase.RegisterUseCase
import com.dmitry.test.animeapplication.domain.usecase.SendCodeUseCase
import com.dmitry.test.animeapplication.domain.usecase.VerifyCodeUseCase
import com.dmitry.test.animeapplication.domain.validation.AuthValidation
import com.dmitry.test.animeapplication.domain.validation.FieldResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val register: RegisterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEmailChange(v: String) = _state.update {
        it.copy(
            email = v,
            emailError = null,
            formError = null
        )
    }

    fun onPasswordChange(v: String) = _state.update {
        it.copy(
            password = v,
            passwordError = null,
            formError = null
        )
    }

    fun onUsernameChange(v: String) = _state.update {
        it.copy(
            username = v,
            usernameError = null,
            formError = null
        )
    }

    fun togglePasswordVisibility() = _state.update {
        it.copy(
            showPassword = !it.showPassword
        )
    }

    fun toggleMode() = _state.update {
        AuthUiState(mode = if (it.mode == AuthMode.Login) AuthMode.Register else AuthMode.Login)
    }

    fun submit() {
        val s = _state.value
        when (s.mode) {
            AuthMode.Login -> {
                val emailRes = AuthValidation.email(s.email)
                val passRes = AuthValidation.password(s.password)
                if (emailRes is FieldResult.Invalid || passRes is FieldResult.Invalid) {
                    _state.update { it.copy(
                        emailError = (emailRes as? FieldResult.Invalid)?.message,
                        passwordError = (passRes as? FieldResult.Invalid)?.message,
                    ) }
                    return
                }
            }

            AuthMode.Register -> {
                val emailRes = AuthValidation.email(s.email)
                val passRes = AuthValidation.password(s.password)
                val userRes = AuthValidation.username(s.username)
                if (emailRes is FieldResult.Invalid || passRes is FieldResult.Invalid || userRes is FieldResult.Invalid) {
                    _state.update { it.copy(
                        emailError = (emailRes as? FieldResult.Invalid)?.message,
                        passwordError = (passRes as? FieldResult.Invalid)?.message,
                        usernameError = (userRes as? FieldResult.Invalid)?.message
                    ) }
                    return
                }
            }
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, formError = null) }

            val result = when (s.mode) {
                AuthMode.Login -> login(s.email, s.password)
                AuthMode.Register -> register(s.email, s.password, s.username)
            }

            when (result) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(AuthEvent.NavigateToHome)
                }
                is AuthResult.EmailNotVerified -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(AuthEvent.NavigateToVerification)
                }
                is AuthResult.Error -> _state.update {
                    it.copy(isLoading = false, formError = result.message)
                }
            }
        }
    }
}





























