package com.dmitry.yume.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.domain.models.User
import com.dmitry.yume.presentation.screens.profile.components.Avatar
import com.dmitry.yume.presentation.screens.profile.components.DisplayName
import com.dmitry.yume.presentation.screens.profile.components.ExitButton
import com.dmitry.yume.presentation.screens.profile.components.Glowing
import com.dmitry.yume.presentation.screens.profile.components.VerifyEmailButton
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onVerificationClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegistrationClick: () -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val state = viewModel.state.collectAsStateWithLifecycle()
    val user = viewModel.user.collectAsStateWithLifecycle()
    val isRefreshing = state.value.isLoading

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        ProfileHolder(
            pullToRefreshState = pullToRefreshState,
            userData = user.value,
            isRefreshing = isRefreshing,
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel,
            onVerificationClick = onVerificationClick,
            onLoginClick = onLoginClick,
            onRegistrationClick = onRegistrationClick,
        )
    }
}

@Composable
fun ProfileHolder(
    userData: User?,
    pullToRefreshState: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier,
    viewModel: ProfileViewModel,
    onVerificationClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegistrationClick: () -> Unit,
) {
    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = modifier
    ) {
        Glowing()

        Column(
            modifier = Modifier
                .fillMaxSize()
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (userData != null) {
                UserContent(
                    userData = userData,
                    onVerificationClick = onVerificationClick,
                    onExitClick = viewModel::logout
                )
            } else {
                LoginContent(
                    onLoginClick = onLoginClick,
                    onRegistrationClick = onRegistrationClick
                )
            }
        }
    }
}
