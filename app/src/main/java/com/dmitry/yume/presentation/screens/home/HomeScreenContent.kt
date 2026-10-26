package com.dmitry.yume.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dmitry.yume.domain.models.AppError
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.ErrorScreen
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreenContent(
    homeData: HomeViewState,
    progressData: ProgressViewState,
    onItemClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onPlayClick: (Int) -> Unit,
    progressActionState: ProgressActionState = ProgressActionState(),
    onRemoveProgress: (Int) -> Unit = {},
    onClearAllProgress: () -> Unit = {},
    onProgressErrorDismiss: () -> Unit = {},
    onRetry: () -> Unit
) {
    AnimatedContent(
        targetState = homeData,
        contentKey = { it::class },
        transitionSpec = { fadeIn() togetherWith fadeOut() using null  },
        label = "home"
    ) { state ->
        when (state) {
            is HomeViewState.Success -> {
                Scaffold(
                    topBar = { HomeTopBar(onSearchClick) },
                ) { innerPadding ->
                    HomeContent(
                        contentPadding = innerPadding,
                        homeData = state.homeData,
                        onItemClick = onItemClick,
                        progressData = progressData,
                        onPlayClick = onPlayClick,
                        progressActionState = progressActionState,
                        onRemoveProgress = onRemoveProgress,
                        onClearAllProgress = onClearAllProgress,
                    )
                }
            }

            is HomeViewState.Loading -> {
                Scaffold(
                    topBar = { HomeTopBar(onSearchClick) },
                ) { innerPadding ->

                    HomePlaceholder(
                        contentPadding = innerPadding
                    )
                }
            }

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

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun HomeScreenContentPreview() {
    YumeTheme {
        PreviewImages {
            HomeScreenContent(
                onItemClick = {},
                onSearchClick = { },
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
