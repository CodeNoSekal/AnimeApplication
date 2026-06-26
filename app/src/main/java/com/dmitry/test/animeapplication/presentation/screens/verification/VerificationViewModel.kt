package com.dmitry.test.animeapplication.presentation.screens.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.test.animeapplication.domain.repository.AuthResult
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
class VerificationViewModel @Inject constructor(
    private val sendCode: SendCodeUseCase,
    private val verifyCode: VerifyCodeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(VerificationUiState())
    val state: StateFlow<VerificationUiState> = _state.asStateFlow()

    private val _events = Channel<VerifyEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()


    fun onCodeChange(v: String) = _state.update {
        it.copy(
            code = v,
            codeError = null,
            formError = null
        )
    }

    fun requestCode(){
        viewModelScope.launch {
            sendCode()
        }
    }

    fun submit() {
        val s = _state.value

        val codeRes = AuthValidation.code(s.code)
        if (codeRes is FieldResult.Invalid) {
            _state.update { it.copy(
                codeError = codeRes.message
            ) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, formError = null) }

            when (val result = verifyCode(s.code)) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(VerifyEvent.NavigateToHome)
                }
                is AuthResult.Error -> _state.update {
                    it.copy(isLoading = false, formError = result.message)
                }

                else -> {}
            }
        }
    }
}