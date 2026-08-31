package com.dmitry.yume.presentation.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.domain.models.User
import com.dmitry.yume.presentation.screens.profile.components.Glowing

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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(user.value?.id, user.value?.emailVerified) {
        if (user.value?.emailVerified == true) viewModel.loadStats()
    }

    LaunchedEffect(state.value.error) {
        state.value.error?.let {
            snackbarHostState.showSnackbar(it)
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
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        ProfileHolder(
            pullToRefreshState = pullToRefreshState,
            userData = user.value,
            isRefreshing = isRefreshing,
            // The transparent bar overlays the header, just like it does on the details screen.
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            viewModel = viewModel,
            onVerificationClick = onVerificationClick,
            onLoginClick = onLoginClick,
            onRegistrationClick = onRegistrationClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileActionsTopBar(
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    TopAppBar(
        title = {},
        modifier = Modifier.height(85.dp),
        navigationIcon = {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Редактировать профиль",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Настройки аккаунта",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0f),
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
                    statistics = viewModel.state.value.statistics,
                    isStatsLoading = viewModel.state.value.isStatsLoading,
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
