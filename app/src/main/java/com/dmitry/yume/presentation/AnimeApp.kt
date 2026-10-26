package com.dmitry.yume.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.presentation.screens.ErrorScreen
import com.dmitry.yume.presentation.screens.SplashScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeApp(rootViewModel: RootViewModel = hiltViewModel()){
    val sessionState by rootViewModel.sessionState.collectAsStateWithLifecycle()

    when (sessionState) {
        SessionState.Loading -> SplashScreen()
        SessionState.Unavailable -> ErrorScreen(
            title = "Невозможно авторизоваться",
            message = "Проверьте подключение к интернету и попробуйте ещё раз",
            onRetry = rootViewModel::retrySessionValidation,
        )
        SessionState.Guest -> RootScreen(sessionState)
        is SessionState.Authenticated -> RootScreen(sessionState)
    }
}
