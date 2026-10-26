package com.dmitry.yume.presentation.screens.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.repository.AuthResult
import com.dmitry.yume.domain.repository.OperationResult
import com.dmitry.yume.domain.usecase.SendCodeUseCase
import com.dmitry.yume.domain.usecase.VerifyCodeUseCase
import com.dmitry.yume.domain.validation.AuthValidation
import com.dmitry.yume.domain.validation.FieldResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val sendCode: SendCodeUseCase,
    private val verifyCode: VerifyCodeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(VerificationViewState())
    val state: StateFlow<VerificationViewState> = _state.asStateFlow()

    private val _events = Channel<VerificationEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    private var codeRequestJob: Job? = null


    fun onCodeChange(v: String) = _state.update {
        it.copy(
            code = v,
            codeError = null,
            formError = null
        )
    }

    fun requestCode(){
        if (codeRequestJob?.isActive == true) return

        codeRequestJob = viewModelScope.launch {
            when (val result = sendCode()) {
                is OperationResult.Success -> Unit
                is OperationResult.Error -> _state.update {
                    it.copy(formError = result.message)
                }
            }
        }
    }

    fun submit() {
        val current = _state.value
        if (current.isLoading) return

        val codeRes = AuthValidation.code(current.code)
        if (codeRes is FieldResult.Invalid) {
            _state.update { it.copy(
                codeError = codeRes.message
            ) }
            return
        }

        _state.value = current.copy(isLoading = true, formError = null)

        viewModelScope.launch {
            when (val result = verifyCode(current.code)) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(VerificationEvent.NavigateToHome)
                }
                is AuthResult.Error -> _state.update {
                    it.copy(isLoading = false, formError = result.message)
                }

                else -> {}
            }
        }
    }
}
