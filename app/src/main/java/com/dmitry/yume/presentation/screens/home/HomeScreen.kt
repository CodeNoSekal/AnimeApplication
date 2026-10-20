package com.dmitry.yume.presentation.screens.home

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.domain.models.AppError
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.ErrorScreen
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun HomeScreen(
    onItemClick: (Int) -> Unit,
    onSearchClicked: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    onPlayClick: (Int) -> Unit,
) {
    val progressData by homeViewModel.progressState.collectAsStateWithLifecycle()
    val homeData by homeViewModel.homeState.collectAsStateWithLifecycle()
    val progressActionState by homeViewModel.progressActionState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        homeViewModel.updateProgress()
    }

    HomeScreenContent(
        homeData, progressData, onItemClick, onSearchClicked, onPlayClick,
        progressActionState = progressActionState,
        onRemoveProgress = homeViewModel::clearProgress,
        onClearAllProgress = homeViewModel::clearAllProgress,
        onProgressErrorDismiss = homeViewModel::dismissProgressActionError,
        onRetry = homeViewModel::load
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreenContent(
    homeData: HomeViewState,
    progressData: ProgressViewState,
    onItemClick: (Int) -> Unit,
    onSearchClicked: () -> Unit,
    onPlayClick: (Int) -> Unit,
    progressActionState: ProgressActionState = ProgressActionState(),
    onRemoveProgress: (Int) -> Unit = {},
    onClearAllProgress: () -> Unit = {},
    onProgressErrorDismiss: () -> Unit = {},
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = { HomeTopBar(onSearchClicked) },
    ) { innerPadding ->
        AnimatedContent(
            targetState = homeData,
            contentKey = { it::class },
            transitionSpec = { fadeIn() togetherWith fadeOut() using null  },
            label = "home"
        ) { state ->
            when (state) {
                is HomeViewState.Success -> HomeContent(
                    contentPadding = innerPadding,
                    homeData = state.homeData,
                    onItemClick = onItemClick,
                    progressData = progressData,
                    onPlayClick = onPlayClick,
                    progressActionState = progressActionState,
                    onRemoveProgress = onRemoveProgress,
                    onClearAllProgress = onClearAllProgress,
                )

                is HomeViewState.Loading -> HomePlaceholder(
                    contentPadding = innerPadding
                )

                is HomeViewState.Error -> {
                    val (title, message) = when (state.error) {
                        AppError.NoConnection -> "Нет подключения" to "Проверьте интернет и попробуйте ещё раз"
                        AppError.NotFound -> "Страница не найдена" to null
                        AppError.Server -> "Сервер недоступен" to "Попробуйте немного позже"
                        AppError.Unknown -> "Не удалось загрузить домашний экран" to null
                    }

                    ErrorScreen(
                        title = title,
                        message = message,
                        onRetry = if (state.error == AppError.NotFound) null else onRetry,
                        modifier = Modifier.systemBarsPadding()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun HomeScreenPreview() {
    YumeTheme {
        PreviewImages {
            HomeScreenContent(
                onItemClick = {},
                onSearchClicked = { },
                onPlayClick = { },
                homeData = HomeViewState.Success(PreviewData.homeData),
                progressData = ProgressViewState.Success(PreviewData.progress),
                progressActionState = ProgressActionState(),
                onRemoveProgress = {},
                onClearAllProgress = {},
                onProgressErrorDismiss = {},
                onRetry = {}
            )
        }
    }
}