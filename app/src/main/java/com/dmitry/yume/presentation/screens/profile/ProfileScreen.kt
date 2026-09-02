package com.dmitry.yume.presentation.screens.profile

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.domain.models.User
import com.dmitry.yume.presentation.screens.profile.components.Glowing
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import com.dmitry.yume.R

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onVerificationClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegistrationClick: () -> Unit,
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val state = viewModel.state.collectAsStateWithLifecycle()
    val user = viewModel.user.collectAsStateWithLifecycle()
    val isRefreshing = state.value.isLoading
    val snackBarHostState = remember { SnackbarHostState() }

    val scrollState = rememberScrollState()
    val startFadePx = with(LocalDensity.current) { 50.dp.toPx() }
    val fadeDistancePx = with(LocalDensity.current) { 80.dp.toPx() }
    val topBarAlpha by remember(startFadePx, fadeDistancePx) {
        derivedStateOf {
            ((scrollState.value - startFadePx) / fadeDistancePx).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(state.value.error) {
        state.value.error?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (user.value != null) {
                ProfileActionsTopBar(
                    onEditClick = onEditClick,
                    onSettingsClick = onSettingsClick,
                    topBarAlpha = topBarAlpha,
                )
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { innerPadding ->
        ProfileHolder(
            pullToRefreshState = pullToRefreshState,
            userData = user.value,
            isRefreshing = isRefreshing,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            viewModel = viewModel,
            onVerificationClick = onVerificationClick,
            onLoginClick = onLoginClick,
            onRegistrationClick = onRegistrationClick,
            scrollState = scrollState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileActionsTopBar(
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
    topBarAlpha: Float,
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onEditClick) {
                Icon(
                    painter = painterResource(R.drawable.star_24),
                    contentDescription = "Редактировать профиль",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    painter = painterResource(R.drawable.settings_24),
                    contentDescription = "Настройки аккаунта",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = topBarAlpha),
        ),
    )
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
    scrollState: ScrollState,
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
                    onExitClick = viewModel::logout,
                    statistics = viewModel.state.collectAsState().value.statistics,
                    isStatsLoading = viewModel.state.collectAsState().value.isStatsLoading,
                    scrollState = scrollState,
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
