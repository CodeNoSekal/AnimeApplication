package com.dmitry.yume.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.domain.models.User
import com.dmitry.yume.domain.repository.SessionRefreshResult
import com.dmitry.yume.domain.repository.OperationResult
import com.dmitry.yume.domain.usecase.LogoutUseCase
import com.dmitry.yume.domain.usecase.ObserveSessionStateUseCase
import com.dmitry.yume.domain.usecase.RefreshCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val refreshUser: RefreshCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val observeSessionState: ObserveSessionStateUseCase
) : ViewModel() {

    val user: StateFlow<User?> =
        observeSessionState()
            .map { sessionState ->
                (sessionState as? SessionState.Authenticated)?.user
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()
    private var refreshJob: Job? = null
    private var logoutJob: Job? = null


    fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = refreshUser()) {
                is SessionRefreshResult.Success ->
                    _state.update { it.copy(isLoading = false, error = null) }
                is SessionRefreshResult.Error ->
                    _state.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    fun logout() {
        if (logoutJob?.isActive == true) return

        refreshJob?.cancel()
        logoutJob = viewModelScope.launch {
            when (val result = logoutUseCase()) {
                is OperationResult.Success -> Unit
                is OperationResult.Error -> _state.update {
                    it.copy(error = result.message)
                }
            }
        }
    }
}
