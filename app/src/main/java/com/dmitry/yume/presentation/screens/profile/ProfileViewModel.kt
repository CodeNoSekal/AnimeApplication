package com.dmitry.yume.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.models.AppError
import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.domain.repository.ProfileResult
import com.dmitry.yume.domain.usecase.GetProfileUseCase
import com.dmitry.yume.domain.usecase.LogoutUseCase
import com.dmitry.yume.domain.usecase.ObserveSessionStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val observeSessionState: ObserveSessionStateUseCase,
    private val getProfile: GetProfileUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<ProfileViewState>(ProfileViewState.Loading)
    val state: StateFlow<ProfileViewState> = _state.asStateFlow()

    private var logoutJob: Job? = null
    private var loadProfileJob: Job? = null

    init {
        viewModelScope.launch {
            observeSessionState().collect { session ->
                when (session) {
                    SessionState.Loading -> {
                        if (_state.value !is ProfileViewState.Content) {
                            _state.value = ProfileViewState.Loading
                        }
                    }

                    SessionState.Guest -> {
                        loadProfileJob?.cancel()
                        _state.value = ProfileViewState.Guest
                    }

                    SessionState.Unavailable -> _state.value =
                        ProfileViewState.Error(AppError.NoConnection)

                    is SessionState.Authenticated -> load()
                }
            }
        }
    }

    fun logout() {
        if (logoutJob?.isActive == true) return
        logoutJob = viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun load() {
        loadProfileJob?.cancel()

        loadProfileJob = viewModelScope.launch {
            if (_state.value !is ProfileViewState.Content) {
                _state.value = ProfileViewState.Loading
            }

            when (val result = getProfile()) {
                is ProfileResult.Success -> {
                    _state.value = ProfileViewState.Content(result.profile)
                }
                is ProfileResult.Error -> {
                    if (_state.value !is ProfileViewState.Content) {
                        _state.value = ProfileViewState.Error(result.error)
                    }
                }
            }
        }
    }
}
